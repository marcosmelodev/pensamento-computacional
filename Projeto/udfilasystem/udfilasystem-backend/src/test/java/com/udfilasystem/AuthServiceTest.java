package com.udfilasystem;

import com.udfilasystem.dto.CadastroRequest;
import com.udfilasystem.dto.CadastroResponse;
import com.udfilasystem.entity.Usuario;
import com.udfilasystem.exception.RecursoExisteException;
import com.udfilasystem.repository.RefreshTokenRepository;
import com.udfilasystem.repository.UsuarioRepository;
import com.udfilasystem.security.JwtService;
import com.udfilasystem.service.AuthService;
import com.udfilasystem.service.TotpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UsuarioRepository    usuarioRepository;
    @Mock RefreshTokenRepository refreshTokenRepository;
    @Mock TotpService          totpService;
    @Mock JwtService           jwtService;
    @Mock PasswordEncoder      passwordEncoder;
    @Mock AuthenticationManager authenticationManager;

    @InjectMocks
    AuthService authService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "refreshExpirationMs", 86400000L);
    }

    @Test
    @DisplayName("Cadastro bem-sucedido retorna QR Code")
    void cadastrar_deveRetornarQrCode() {
        CadastroRequest request = new CadastroRequest("Joao Silva", "joao@teste.com", "Senha@123", "Senha@123");

        when(usuarioRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(request.senha())).thenReturn("$2a$12$hash");
        when(totpService.gerarSegredo()).thenReturn("JBSWY3DPEHPK3PXP");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> {
            Usuario u = inv.getArgument(0);
            ReflectionTestUtils.setField(u, "id", 1L);
            return u;
        });
        when(totpService.gerarQrCodeBase64(any(), any())).thenReturn("data:image/png;base64,AAAAA");

        CadastroResponse response = authService.cadastrar(request);

        assertThat(response.email()).isEqualTo("joao@teste.com");
        assertThat(response.qrCodeBase64()).startsWith("data:image/png;base64,");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Cadastro com e-mail duplicado lanca excecao")
    void cadastrar_emailDuplicado_lancaExcecao() {
        CadastroRequest request = new CadastroRequest("Joao Silva", "joao@teste.com", "Senha@123", "Senha@123");

        when(usuarioRepository.existsByEmail(request.email())).thenReturn(true);

        assertThatThrownBy(() -> authService.cadastrar(request))
                .isInstanceOf(RecursoExisteException.class)
                .hasMessageContaining("E-mail ja cadastrado");
    }

    @Test
    @DisplayName("Codigo TOTP invalido na etapa 2 lanca excecao")
    void autenticarEtapa2_codigoInvalido_lancaExcecao() {
        var request = new com.udfilasystem.dto.LoginEtapa2Request("joao@teste.com", 123456);

        Usuario usuario = Usuario.builder()
                .email("joao@teste.com")
                .totpSecret("JBSWY3DPEHPK3PXP")
                .ativo(true)
                .build();

        when(usuarioRepository.findByEmailAndAtivoTrue("joao@teste.com")).thenReturn(Optional.of(usuario));
        when(totpService.verificarCodigo("JBSWY3DPEHPK3PXP", 123456)).thenReturn(false);

        assertThatThrownBy(() -> authService.autenticarEtapa2(request))
                .isInstanceOf(com.udfilasystem.exception.AutenticacaoException.class)
                .hasMessageContaining("invalido");
    }
}
