# Erros Identificados

## 1. Problema de CORS

### Erro
O frontend não conseguia acessar a API backend devido ao bloqueio de CORS.

### Causa
A classe CorsConfig não possuía a anotação @Configuration, impedindo o carregamento pelo Spring.



    package com.udfilasystem.config;
    
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.web.servlet.config.annotation.CorsRegistry;
    import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
    
    public class CorsConfig {
        @Bean
        public WebMvcConfigurer corsConfigurer() {
    
            return new WebMvcConfigurer() {
    
                @Override
                public void addCorsMappings(CorsRegistry registry) {
    
                    registry.addMapping("/**")
                            .allowedOrigins("http://localhost:5173")
                            .allowedMethods("*")
                            .allowedHeaders("*");
                }
            };
        }
    }


### Solução
Foi adicionada a anotação @Configuration e validada a configuração diretamente no SecurityConfig.

    package com.udfilasystem.config;
    
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.web.servlet.config.annotation.CorsRegistry;
    import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
    
    @Configuration
    public class CorsConfig {
        @Bean
        public WebMvcConfigurer corsConfigurer() {
    
            return new WebMvcConfigurer() {
    
                @Override
                public void addCorsMappings(CorsRegistry registry) {
    
                    registry.addMapping("/**")
                            .allowedOrigins("http://localhost:5173")
                            .allowedMethods("*")
                            .allowedHeaders("*");
                }
            };
        }
    }

---

## 2. Frontend não inicializava

### Erro
O comando npm run dev retornava erro de execução.

### Causa
O comando estava sendo executado fora da pasta do projeto frontend.

### Solução
Foi acessada corretamente a pasta do frontend contendo o arquivo package.json.

---

## 3. Incompatibilidade Node.js

### Erro
O npm install apresentava erro:
"Cannot read properties of null (reading 'matches')"

### Causa
Uso da versão Node.js 24, incompatível com algumas dependências do projeto.

### Solução
Atualização para versão LTS estável do Node.js.

---

## 4. Erro de entendimento entre frontend e backend

### Erro
Tentativa de acessar localhost:8080 diretamente pelo navegador esperando interface visual.

### Causa
A aplicação backend era apenas uma API REST sem frontend embutido.

### Solução
Inicialização separada do frontend React/Vite na porta 5173.
