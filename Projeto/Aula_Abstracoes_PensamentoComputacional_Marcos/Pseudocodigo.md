algoritmo "Fluxo_Cadastro_Usuario"

var
    opcao, nomeCompleto, matricula, email, perfil, senha, confirmarSenha: cadeia
    perfilValido: lógico

inicio
    escreva("=== Página Inicial ===")
    escreva("Escolha uma opção: [1] Login  [2] Cadastrar-se")
    leia(opcao)

    se opcao = "1" entao
        escreva("Redirecionando para página de login...")
        // Aqui seria chamada a função de autenticação
        escreva("Digite seu e-mail e senha para acessar o sistema.")
    senao
        se opcao = "2" entao
            escreva("=== Cadastro de Usuário ===")
            
            escreva("Digite seu nome completo: ")
            leia(nomeCompleto)
            
            escreva("Digite sua matrícula (ex: 2024001): ")
            leia(matricula)
            
            escreva("Digite seu e-mail: ")
            leia(email)
            
            escreva("Selecione seu perfil (Aluno, Atendente, Administrador): ")
            leia(perfil)
            
            perfilValido <- (perfil = "Aluno") ou (perfil = "Atendente") ou (perfil = "Administrador")
            
            se perfilValido entao
                escreva("Digite sua senha (mínimo 8 caracteres): ")
                leia(senha)
                
                escreva("Confirme sua senha: ")
                leia(confirmarSenha)
                
                se senha = confirmarSenha entao
                    se comprimento(senha) >= 8 entao
                        escreva("Conta criada com sucesso!")
                    senao
                        escreva("Erro: A senha deve ter pelo menos 8 caracteres.")
                    fimse
                senao
                    escreva("Erro: As senhas não coincidem.")
                fimse
            senao
                escreva("Erro: Perfil inválido. Acesso negado.")
            fimse
        senao
            escreva("Opção inválida. Retorne à página inicial.")
        fimse
    fimse

fimalgoritmo
