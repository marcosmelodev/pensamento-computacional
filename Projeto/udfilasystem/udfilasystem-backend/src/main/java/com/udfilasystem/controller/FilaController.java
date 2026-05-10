package com.udfilasystem.controller;

import com.udfilasystem.dto.EntrarFilaResponse;
import com.udfilasystem.dto.FilaDto;
import com.udfilasystem.dto.FilaRequest;
import com.udfilasystem.dto.FilaStatusDto;
import com.udfilasystem.service.FilaService;
import java.util.List;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/filas")
@RequiredArgsConstructor
public class FilaController {

    private final FilaService filaService;

    /**
     * GET /api/filas/status
     * Retorna status em tempo real de todas as filas por setor.
     * Consumido pelo dashboard do usuário (sem paginação).
     */
    @GetMapping("/status")
    public ResponseEntity<List<FilaStatusDto>> statusPorSetor() {
        return ResponseEntity.ok(filaService.obterStatusPorSetor());
    }

    /**
     * GET /api/filas
     * Lista todas as filas ativas (paginado).
     */
    @GetMapping
    public ResponseEntity<Page<FilaDto>> listarFilas(
            @PageableDefault(size = 20, sort = "nome") Pageable pageable
    ) {
        return ResponseEntity.ok(filaService.listarFilasAtivas(pageable));
    }

    /**
     * POST /api/filas
     * Cria nova fila — apenas ADMIN.
     */
    @PostMapping
    public ResponseEntity<FilaDto> criarFila(
            @Valid @RequestBody FilaRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        FilaDto fila = filaService.criarFila(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(fila);
    }

    /**
     * POST /api/filas/{filaId}/entrar
     * Entra na fila especificada.
     */
    @PostMapping("/{filaId}/entrar")
    public ResponseEntity<EntrarFilaResponse> entrarNaFila(
            @PathVariable Long filaId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(filaService.entrarNaFila(filaId, userDetails.getUsername()));
    }

    /**
     * DELETE /api/filas/{filaId}/sair
     * Sai da fila especificada.
     */
    @DeleteMapping("/{filaId}/sair")
    public ResponseEntity<Void> sairDaFila(
            @PathVariable Long filaId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        filaService.sairDaFila(filaId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
