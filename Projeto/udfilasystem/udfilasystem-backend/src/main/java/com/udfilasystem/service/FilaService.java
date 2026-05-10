package com.udfilasystem.service;

import com.udfilasystem.dto.EntrarFilaResponse;
import com.udfilasystem.dto.FilaDto;
import com.udfilasystem.dto.FilaRequest;
import com.udfilasystem.dto.FilaStatusDto;
import com.udfilasystem.entity.EntradaFila;
import com.udfilasystem.entity.Fila;
import com.udfilasystem.entity.TipoSetor;
import com.udfilasystem.entity.Usuario;
import com.udfilasystem.exception.AutenticacaoException;
import com.udfilasystem.exception.RegraDeNegocioException;
import com.udfilasystem.repository.EntradaFilaRepository;
import com.udfilasystem.repository.FilaRepository;
import com.udfilasystem.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilaService {

    private final FilaRepository        filaRepository;
    private final EntradaFilaRepository entradaFilaRepository;
    private final UsuarioRepository     usuarioRepository;

    /** Tempo médio de atendimento em minutos por setor. */
    private static int tempoMedioPorSetor(TipoSetor tipo) {
        return switch (tipo) {
            case COORDENACAO -> 12;
            case FINANCEIRO  -> 8;
            case SECRETARIA  -> 6;
        };
    }

    @Transactional(readOnly = true)
    public Page<FilaDto> listarFilasAtivas(Pageable pageable) {
        return filaRepository.findByAtivaTrue(pageable)
                .map(f -> new FilaDto(
                        f.getId(), f.getNome(), f.getDescricao(),
                        f.getTipo() != null ? f.getTipo().name() : null,
                        f.getTipo() != null ? f.getTipo().getPrefixo() : null,
                        f.getMaxCapacidade(),
                        filaRepository.contarAguardando(f.getId())
                ));
    }

    /**
     * Retorna o status em tempo real de todas as filas por setor.
     * Endpoint: GET /api/filas/status
     */
    @Transactional(readOnly = true)
    public List<FilaStatusDto> obterStatusPorSetor() {
        return Arrays.stream(TipoSetor.values()).map(tipo -> {
            Fila fila = filaRepository.findByTipoAndAtivaTrue(tipo).orElse(null);

            if (fila == null) {
                return new FilaStatusDto(null, tipo.name(), tipo.getDescricao(),
                        tipo.getPrefixo(), 0, "-", tempoMedioPorSetor(tipo));
            }

            long aguardando = filaRepository.contarAguardando(fila.getId());

            // Última senha com status CHAMADO (atendimento em curso)
            String atualAtendendo = entradaFilaRepository
                    .findTopByFilaIdAndStatusOrderByPosicaoAsc(fila.getId(), EntradaFila.Status.CHAMADO)
                    .map(EntradaFila::getCodigo)
                    .orElse("-");

            return new FilaStatusDto(
                    fila.getId(), tipo.name(), tipo.getDescricao(),
                    tipo.getPrefixo(), aguardando,
                    atualAtendendo, tempoMedioPorSetor(tipo)
            );
        }).toList();
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public FilaDto criarFila(FilaRequest request, String emailAdmin) {
        Usuario admin = usuarioRepository.findByEmailAndAtivoTrue(emailAdmin)
                .orElseThrow(() -> new AutenticacaoException("Admin nao encontrado"));

        Fila fila = Fila.builder()
                .nome(request.nome())
                .descricao(request.descricao())
                .tipo(request.tipo())
                .maxCapacidade(request.maxCapacidade())
                .criadoPor(admin)
                .build();

        fila = filaRepository.save(fila);
        log.info("Fila criada: {} ({}) por {}", fila.getNome(), fila.getTipo(), emailAdmin);

        return new FilaDto(fila.getId(), fila.getNome(), fila.getDescricao(),
                fila.getTipo().name(), fila.getTipo().getPrefixo(),
                fila.getMaxCapacidade(), 0);
    }

    @Transactional
    public EntrarFilaResponse entrarNaFila(Long filaId, String emailUsuario) {
        Fila fila = filaRepository.findById(filaId)
                .filter(Fila::isAtiva)
                .orElseThrow(() -> new RegraDeNegocioException("Fila nao encontrada ou inativa"));

        Usuario usuario = usuarioRepository.findByEmailAndAtivoTrue(emailUsuario)
                .orElseThrow(() -> new AutenticacaoException("Usuario nao encontrado"));

        long qtdAguardando = filaRepository.contarAguardando(filaId);
        if (qtdAguardando >= fila.getMaxCapacidade()) {
            throw new RegraDeNegocioException(
                    "Fila atingiu a capacidade maxima de " + fila.getMaxCapacidade() + " usuarios");
        }

        entradaFilaRepository
                .findByFilaIdAndUsuarioIdAndStatus(filaId, usuario.getId(), EntradaFila.Status.AGUARDANDO)
                .ifPresent(e -> { throw new RegraDeNegocioException("Voce ja esta nesta fila"); });

        int proximaPosicao = entradaFilaRepository.obterMaximaPosicao(filaId) + 1;

        // Gera código da senha: prefixo do setor + posição com 2 dígitos (ex: C01, F09, S14)
        String codigo = String.format("%s%02d", fila.getTipo().getPrefixo(), proximaPosicao);

        EntradaFila entrada = EntradaFila.builder()
                .fila(fila)
                .usuario(usuario)
                .posicao(proximaPosicao)
                .codigo(codigo)
                .status(EntradaFila.Status.AGUARDANDO)
                .build();

        entrada = entradaFilaRepository.save(entrada);
        log.info("Usuario {} entrou na fila {} — senha {} (posicao {})",
                emailUsuario, filaId, codigo, proximaPosicao);

        int pessoasNaFrente = proximaPosicao - 1;
        int previsaoMin     = pessoasNaFrente * tempoMedioPorSetor(fila.getTipo());

        return new EntrarFilaResponse(
                entrada.getId(), filaId, fila.getNome(),
                fila.getTipo().name(), codigo,
                proximaPosicao, pessoasNaFrente, previsaoMin,
                entrada.getEntrouEm()
        );
    }

    @Transactional
    public void sairDaFila(Long filaId, String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmailAndAtivoTrue(emailUsuario)
                .orElseThrow(() -> new AutenticacaoException("Usuario nao encontrado"));

        EntradaFila entrada = entradaFilaRepository
                .findByFilaIdAndUsuarioIdAndStatus(filaId, usuario.getId(), EntradaFila.Status.AGUARDANDO)
                .orElseThrow(() -> new RegraDeNegocioException("Voce nao esta nesta fila"));

        entrada.setStatus(EntradaFila.Status.CANCELADO);
        entrada.setAtendidoEm(LocalDateTime.now());
        entradaFilaRepository.save(entrada);

        log.info("Usuario {} cancelou a senha na fila {}", emailUsuario, filaId);
    }

    @Transactional(readOnly = true)
    public List<EntradaFila> listarFilaOrdenada(Long filaId) {
        return entradaFilaRepository
                .findByFilaIdAndStatusOrderByPosicaoAsc(filaId, EntradaFila.Status.AGUARDANDO);
    }
}
