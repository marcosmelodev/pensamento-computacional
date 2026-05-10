package com.udfilasystem.config;

import com.udfilasystem.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Limpa refresh tokens expirados ou revogados toda madrugada (01:00).
     */
    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void limparTokensExpirados() {
        int removidos = refreshTokenRepository.limparTokensExpirados();
        log.info("Limpeza de tokens: {} registros removidos", removidos);
    }
}
