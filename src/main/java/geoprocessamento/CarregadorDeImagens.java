package geoprocessamento; // mesmo pacote das outras classes

/**
 * Programa SEPARADO (tem o próprio main) usado só para gerar a massa de dados.
 * Ele lê todas as fotos .jpg de uma pasta e insere cada uma no banco
 * com data, hora e UF sorteadas.
 */
public class CarregadorDeImagens {
    public static void main(String[] args) { // ponto de partida deste programa

        java.io.File pasta = new java.io.File("C:/Users/****/Downloads/Apsimagens/Apsimagens"); // pasta onde estão as fotos (caminho fixo neste computador)
        java.io.File[] arquivos = pasta.listFiles(); // listFiles() pega todos os arquivos da pasta e guarda num vetor

        System.out.println("Achei " + arquivos.length + " arquivos."); // mostra no console quantos arquivos encontrou

        int contador = 0; // contador de fotos .jpg

        for (java.io.File arquivo : arquivos) { // percorre cada arquivo da pasta (for-each)
            if (arquivo.getName().toLowerCase().endsWith(".jpg")) { // toLowerCase deixa o nome em minúsculas, assim .JPG também conta
                contador++; // soma 1 no contador
            }
        }

        System.out.println("Fotos .jpg: " + contador); // mostra quantas fotos .jpg existem

        int limite = arquivos.length; // limite de fotos a inserir (igual ao total, então na prática não limita)

        // vetor com as 27 UFs do Brasil, usado para sortear o estado de cada foto
        String[] ufs = {"AC","AL","AP","AM","BA","CE","DF","ES","GO","MA","MT","MS","MG",
                "PA","PB","PR","PE","PI","RJ","RN","RS","RO","RR","SC","SP","SE","TO"};

        java.util.Random sorteio = new java.util.Random(); // Random é o "sorteador" de números aleatórios

        // comando SQL de inserção; cada ? será preenchido depois, na mesma ordem das colunas
        String sql = "INSERT INTO cadastro_imagem "
                + "(data_cadastro, hora_cadastro, uf, nome_imagem, imagem) "
                + "VALUES (?, ?, ?, ?, ?)";

        // try-with-resources: abre a conexão e prepara o comando UMA vez só;
        // no final, fecha os dois automaticamente
        try (java.sql.Connection con = Geoprocessamento.conecta();
             java.sql.PreparedStatement stmt = con.prepareStatement(sql)) {

            System.out.println("Conectou no banco!");
            con.setAutoCommit(false);   // só confirma no final, em vez de um por um (muito mais rápido)


            int inseridas = 0; // quantas fotos já foram inseridas

            for (java.io.File arquivo : arquivos) { // percorre cada arquivo da pasta

                if (inseridas >= limite) {
                    break;      // já inseriu o limite, para o laço
                }

                if (!arquivo.getName().toLowerCase().endsWith(".jpg")) {
                    continue;   // não é jpg, pula para o próximo
                }

                // sorteia data, hora e UF (igual ao GeradorDeMassa)
                java.time.LocalDate data = java.time.LocalDate.of(2020, 1, 1) // parte de 01/01/2020...
                        .plusDays(sorteio.nextInt(365 * 7));                  // ...e soma até 7 anos de dias (datas entre 2020 e 2026)
                java.time.LocalTime hora = java.time.LocalTime.of(
                        sorteio.nextInt(24), sorteio.nextInt(60), sorteio.nextInt(60)); // hora (0-23), minuto (0-59) e segundo (0-59)
                String uf = ufs[sorteio.nextInt(ufs.length)]; // sorteia uma posição do vetor de UFs

                // abre a foto e envia junto com os outros dados
                try (java.io.FileInputStream entrada = new java.io.FileInputStream(arquivo)) { // FileInputStream lê o conteúdo do arquivo
                    stmt.setDate(1, java.sql.Date.valueOf(data));   // 1º ? = data
                    stmt.setTime(2, java.sql.Time.valueOf(hora));   // 2º ? = hora
                    stmt.setString(3, uf);                          // 3º ? = UF
                    stmt.setString(4, arquivo.getName());           // 4º ? = nome do arquivo
                    stmt.setBinaryStream(5, entrada, arquivo.length()); // 5º ? = a foto em binário (BLOB)
                    stmt.executeUpdate();                           // executa o INSERT
                }

                inseridas++; // soma 1 nas inseridas

                if (inseridas % 1000 == 0) { // % é o resto da divisão: a cada 1000 fotos...
                    System.out.println(inseridas + " inseridas..."); // ...mostra o progresso
                }
            }
            con.commit();   // grava tudo no banco
            System.out.println(inseridas + " fotos inseridas."); // mostra o total

        } catch (Exception erro) { // se der qualquer erro, cai aqui
            System.out.println("Erro: " + erro.getMessage()); // mostra a mensagem do erro no console
        }

    }
}
