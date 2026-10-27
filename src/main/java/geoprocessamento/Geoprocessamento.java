package geoprocessamento; // esta classe faz parte do pacote geoprocessamento

import javax.swing.*;                        // janelas, botões, tabelas, checkboxes e caixas de mensagem (o * importa tudo do pacote)
import java.awt.*;                           // cores, tamanhos (Dimension), layouts (BorderLayout, GridLayout) e imagens
import java.awt.event.*;                     // eventos, como o de fechar uma janela (WindowAdapter, WindowEvent)
import javax.swing.table.DefaultTableModel;  // o "armazém" onde ficam os dados de uma JTable
import java.sql.Connection;                  // representa a conexão aberta com o banco
import java.sql.DriverManager;               // abre a conexão com o banco
import java.sql.SQLException;                // tipo de erro que acontece em comandos SQL

/**
 * TELA PRINCIPAL do sistema.
 * extends JFrame = HERANÇA: esta classe "é" uma janela e herda tudo de JFrame.
 * Aqui ficam: a conexão com o banco, a tabela de registros, os botões
 * (Cadastrar, Excluir, Editar, Ver imagem) e a parte de ordenação com cronômetro.
 */
public class Geoprocessamento extends JFrame {
    // ATRIBUTOS: variáveis que todos os métodos desta classe podem usar
    private JTable tabelaTempo;  // tabela dos tempos (Bubble, Insertion, Quick)
    private JTable tabelaRegistros;  // tabela dos registros (ID, Data, Hora, UF, Nome)
    private DefaultTableModel RegistroImagens; // "armazém" das linhas dos registros. Mexeu aqui, a tabela atualiza


    // CONSTRUTOR: roda sozinho quando a janela é criada (new Geoprocessamento()) e monta a tela inteira
    public Geoprocessamento(){

        // ---------- configuração da janela ----------
        setTitle("Geoprocessamento");                 // título que aparece no topo da janela
        setSize(700, 750);                            // 700 de largura por 750 de altura
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // fechar esta janela encerra o programa inteiro
        setLocationRelativeTo(null);                  // abre a janela centralizada na tela
        getContentPane().setBackground(Color.WHITE);  // fundo branco

        // ---------- painéis (as "caixas" que organizam a tela) ----------
        JPanel painelPrincipal = new JPanel(new GridLayout(1,2,10,10)); // GridLayout(1,2,10,10) = 1 linha, 2 colunas, 10 pixels de espaço
        setLayout(new BorderLayout(10, 10));          // a janela usa BorderLayout: divide em NORTH, SOUTH, CENTER, EAST e WEST
        JPanel painelOrdenar = new JPanel();          // painel da esquerda: checkboxes e botões de ordenação
        painelOrdenar.setBackground(Color.WHITE);
        JPanel painelCronometro = new JPanel();       // painel da direita: tabela com os tempos
        painelCronometro.setBackground(Color.WHITE);
        JPanel painelDRegistros = new JPanel();       // painel de baixo: tabela de registros e botões
        painelDRegistros.setBackground(Color.WHITE);

        painelPrincipal.add(painelOrdenar);           // coloca o painel de ordenar na 1ª coluna
        painelPrincipal.add(painelCronometro);        // coloca o painel do cronômetro na 2ª coluna (lado a lado)
        painelPrincipal.setPreferredSize(new Dimension(0, 300)); // parte de cima com 300 pixels de altura

        add(painelPrincipal, BorderLayout.NORTH);     // parte de cima da janela
        add(painelDRegistros, BorderLayout.CENTER);   // centro da janela (ocupa o resto)

        // ---------- checkboxes dos métodos de ordenação ----------
        JCheckBox ChkBubbleSort = new JCheckBox("Bubble Sort");       // caixinha de marcar
        ChkBubbleSort.setOpaque(false);                                // fundo transparente (fica com o branco do painel)
        JCheckBox ChkInsertionSort = new JCheckBox("Insertion Sort");
        ChkInsertionSort.setOpaque(false);
        JCheckBox ChkQuickSort = new JCheckBox("Quick Sort");
        ChkQuickSort.setOpaque(false);

         // Painelzinho que segura os 3 checkboxes um embaixo do outro (3 linhas, 1 coluna).
        JPanel painelOpcoes = new JPanel(new GridLayout(3,1));
        painelOpcoes.setOpaque(false);
        painelOpcoes.add(ChkBubbleSort);
        painelOpcoes.add(ChkInsertionSort);
        painelOpcoes.add(ChkQuickSort);

        // ---------- botões Ordenar e Desordenar ----------
        JButton btnIniciar = new JButton("Ordenar");       // botão Ordenar (variável se chama btnIniciar)
        JButton btnDesordenar = new JButton("Desordenar"); // botão Desordenar
        JPanel painelBotao = new JPanel();                  // painel que segura os dois botões lado a lado
        painelBotao.setOpaque(false);
        painelBotao.add(btnIniciar);
        painelBotao.add(btnDesordenar);
        painelOrdenar.setLayout(new BorderLayout());        // painel de ordenar passa a usar BorderLayout
        painelOrdenar.setBorder(BorderFactory.createTitledBorder("Metodos De Ordenação")); // borda com título
        painelOrdenar.add(painelOpcoes, BorderLayout.NORTH); // checkboxes em cima
        painelOrdenar.add(painelBotao, BorderLayout.SOUTH);  // botões embaixo

        // addActionListener "escuta" o clique; "e -> ..." é uma LAMBDA que diz o que fazer no clique
        btnDesordenar.addActionListener(e -> desordenarTabela()); // clique em Desordenar chama o método desordenarTabela()

        // ===================== BOTÃO ORDENAR =====================
        btnIniciar.addActionListener(e -> { // tudo dentro deste bloco roda quando clicam em Ordenar

                    int totalLinhas = RegistroImagens.getRowCount(); // conta quantas linhas a tabela tem
                    if (totalLinhas == 0) { // se não tem nenhum registro...
                        JOptionPane.showMessageDialog(this, "Não há registros para ordenar."); // ...avisa
                        return; // return interrompe o clique aqui; nada abaixo roda
                    }
                    // ! = "não" e && = "e": se NENHUM dos três estiver marcado...
                    if (!ChkBubbleSort.isSelected() && !ChkInsertionSort.isSelected() && !ChkQuickSort.isSelected()) {
                        JOptionPane.showMessageDialog(this, "Marque pelo menos um método.");
                        return;
                    }

                    // copia a tabela da tela para uma MATRIZ: [linhas][5 colunas] (ID, Data, Hora, UF, Nome)
                    Object[][] original = new Object[totalLinhas][5];
                    for (int i = 0; i < totalLinhas; i++) {   // laço de fora: anda pelas linhas
                        for (int c = 0; c < 5; c++) {         // laço de dentro: anda pelas colunas
                            original[i][c] = RegistroImagens.getValueAt(i, c); // copia o valor da linha i, coluna c
                        }
                    }

                    Object[][] ultima = null;   // guarda o último resultado ordenad (começa vazia)

                    for (int i = 0; i < 3; i++) {             // para as 3 linhas do cronômetro...
                        tabelaTempo.setValueAt("-", i, 1);    // ...coloca "-" no tempo (apaga o tempo anterior)
                    }

                    // ----- Bubble Sort (só roda se o checkbox estiver marcado) -----
                    if (ChkBubbleSort.isSelected()) {
                        Object[][] copia = original.clone();  // CÓPIA: cada método ordena os mesmos dados bagunçados
                        long inicio = System.nanoTime();      // marca o início (em nanossegundos)
                        MetodosDeOrdenacao.bubbleSort(copia); // ordena
                        long fim = System.nanoTime();         // marca o fim
                        // (fim - inicio) / 1_000_000 converte nanossegundos em MILISSEGUNDOS; vai na linha 0, coluna 1
                        tabelaTempo.setValueAt(String.valueOf((fim - inicio) / 1_000_000), 0, 1);
                        ultima = copia;                       // guarda o resultado ordenado
                    }

                    // ----- Insertion Sort (mesma estrutura; tempo vai na linha 1) -----
                    if (ChkInsertionSort.isSelected()) {
                        Object[][] copia = original.clone();
                        long inicio = System.nanoTime();
                        MetodosDeOrdenacao.insertionSort(copia);
                        long fim = System.nanoTime();
                        tabelaTempo.setValueAt(String.valueOf((fim - inicio) / 1_000_000), 1, 1);
                        ultima = copia;
                    }

                    // ----- Quick Sort (mesma estrutura; tempo vai na linha 2) -----
                    // obs: são três if separados (não else if), então dá para rodar os três juntos
                    if (ChkQuickSort.isSelected()) {
                        Object[][] copia = original.clone();
                        long inicio = System.nanoTime();
                        MetodosDeOrdenacao.quickSort(copia);
                        long fim = System.nanoTime();
                        tabelaTempo.setValueAt(String.valueOf((fim - inicio) / 1_000_000), 2, 1);
                        ultima = copia;
                    }

                    // mostra o resultado ordenado na tabela da tela
                    if (ultima != null) {                 // se pelo menos um método rodou...
                        RegistroImagens.setRowCount(0);   // ...apaga todas as linhas da tabela
                        for (Object[] linha : ultima) {   // percorre cada linha da matriz ordenada
                            RegistroImagens.addRow(linha); // e adiciona de volta na tabela
                        }
                    }
                }); // fim do bloco do botão Ordenar



        // ===================== TABELA DO CRONÔMETRO =====================
        String[] colunasCronometro = {"Ordenação", "Tempo de Ordenação (ms)"}; // VETOR (um colchete): nomes das colunas
        String[][] dadosCronometro = {   // MATRIZ (dois colchetes): linhas e colunas com os dados iniciais
        {"Bubble Sort", "-"},
        {"Insertion Sort", "-"},
        {"Quick Sort", "-"}};


        tabelaTempo = new JTable(dadosCronometro, colunasCronometro); // cria a tabela juntando dados e colunas
        painelCronometro.setLayout(new BorderLayout());
        painelCronometro.setBorder(BorderFactory.createTitledBorder("Tempo de Ordenaco")); // borda com título
        painelCronometro.add(new JScrollPane(tabelaTempo), BorderLayout.CENTER); // JScrollPane é OBRIGATÓRIO pra o cabeçalho da tabela aparecer. CENTER = ocupa o painel todo

        // ===================== TABELA DE REGISTROS =====================
        String[] colunasRegistro = {"ID", "Data", "Hora", "UF", "Nome da Imagem"}; // nomes das colunas
        RegistroImagens = new DefaultTableModel(colunasRegistro, 0);           // modelo com essas colunas e 0 linhas


        // ---------- botões dos registros ----------
        JButton btnNovo = new JButton("Cadastrar");  // botão Cadastrar (variável btnNovo)
        JButton btnExcluir = new JButton("Excluir");
        JButton btnEditar = new JButton("Editar");
        JButton btnVer = new JButton("Ver imagem");

        // ===================== BOTÃO CADASTRAR =====================
        btnNovo.addActionListener(e -> {
            CadastroImagem telaCadastro = new CadastroImagem(); // abre o formulário VAZIO (construtor sem parâmetros)

            // "vigia" da janela: quando o formulário for fechado, recarrega a tabela
            telaCadastro.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) { // roda quando a janela de cadastro fecha
                    carregarImagens();                     // busca os dados de novo no banco (já com o registro novo)
                }
        });

            telaCadastro.setVisible(true); // mostra o formulário
        });

        // ===================== BOTÃO EDITAR =====================
        btnEditar.addActionListener(e -> {

            int linha = tabelaRegistros.getSelectedRow(); // número da linha selecionada (-1 se nenhuma)

            if (linha == -1) { // nenhuma linha selecionada
                JOptionPane.showMessageDialog(this, "Selecione uma imagem para editar.");
                return;
            }



    // pega os 5 valores da linha selecionada; toString() transforma cada valor em texto (String)
    String id = RegistroImagens.getValueAt(linha, 0).toString();
    String data = RegistroImagens.getValueAt(linha, 1).toString();
    String hora = RegistroImagens.getValueAt(linha, 2).toString();
    String uf = RegistroImagens.getValueAt(linha, 3).toString();
    String nomeImagem = RegistroImagens.getValueAt(linha, 4).toString();

    // abre o formulário JÁ PREENCHIDO (construtor com 5 parâmetros)
    CadastroImagem telaCadastro =
        new CadastroImagem(id, data, hora, uf, nomeImagem);
            telaCadastro.addWindowListener(new WindowAdapter() { // mesmo "vigia" do Cadastrar
                @Override
                public void windowClosed(WindowEvent e) {
                    carregarImagens(); // recarrega a tabela quando o formulário fechar
                }
            });
    telaCadastro.setVisible(true);      // <- linha 115
        });



        // ===================== BOTÃO EXCLUIR =====================
        btnExcluir.addActionListener(e -> {
            int linha = tabelaRegistros.getSelectedRow(); // linha selecionada

            if (linha == -1) { // nenhuma linha selecionada
                JOptionPane.showMessageDialog(this, "Selecione uma imagem para excluir.");
                return;
            }

            String id = RegistroImagens.getValueAt(linha, 0).toString();   // ID do registro (coluna 0)
            String nome = RegistroImagens.getValueAt(linha, 4).toString(); // nome da imagem (coluna 4)

            // caixinha de confirmação com os botões Sim e Não; o clique fica guardado em "resposta"
            int resposta = JOptionPane.showConfirmDialog(this,
                    "Excluir a imagem " + nome + "?",
                    "Confirmar exclusão", JOptionPane.YES_NO_OPTION);

            if (resposta != JOptionPane.YES_OPTION) { // != = "diferente de": se não clicou em Sim...
                return;                               // ...cancela, nada é excluído
            }

            String sql = "DELETE FROM cadastro_imagem WHERE id = ?"; // comando que apaga o registro; o ? é preenchido depois

            // try-with-resources: abre a conexão e prepara o comando; fecha tudo sozinho no final
            try (Connection con = conecta();
                 java.sql.PreparedStatement stmt = con.prepareStatement(sql)) {

                stmt.setInt(1, Integer.parseInt(id)); // coloca o ID no lugar do ?; parseInt transforma texto em número
                stmt.executeUpdate();                 // executa o DELETE
                carregarImagens();                    // recarrega a tabela (a linha some)

            } catch (Exception erro) { // se der erro, mostra a mensagem
                JOptionPane.showMessageDialog(this, "Erro ao excluir: " + erro.getMessage());
            }
        });

        // ===================== BOTÃO VER IMAGEM =====================
        btnVer.addActionListener(e -> {
            int linha = tabelaRegistros.getSelectedRow(); // linha selecionada

            if (linha == -1) {
                JOptionPane.showMessageDialog(this, "Selecione um registro para ver a imagem.");
                return;
            }

            String id = RegistroImagens.getValueAt(linha, 0).toString();   // ID
            String nome = RegistroImagens.getValueAt(linha, 4).toString(); // nome
            verImagem(id, nome); // chama o método que busca e mostra a imagem
        });

        // ---------- montagem da parte de registros ----------
        tabelaRegistros = new JTable(RegistroImagens); // tabela da tela ligada ao modelo (o modelo guarda, a tabela mostra)
        tabelaRegistros.setRowHeight(22);              // altura de cada linha: 22 pixels

        JPanel painelBtnRegistros = new JPanel();      // painel com os 4 botões lado a lado
        painelBtnRegistros.setOpaque(false);
        painelBtnRegistros.add(btnNovo);
        painelBtnRegistros.add(btnExcluir);
        painelBtnRegistros.add(btnEditar);
        painelBtnRegistros.add(btnVer);

        painelDRegistros.setLayout(new BorderLayout());
        painelDRegistros.setBorder(BorderFactory.createTitledBorder("Registros"));   // borda com título
        painelDRegistros.add(new JScrollPane(tabelaRegistros), BorderLayout.CENTER); // tabela no centro, com barra de rolagem
        painelDRegistros.add(painelBtnRegistros, BorderLayout.SOUTH);               // botões embaixo

       carregarImagens(); // busca os registros no banco assim que a janela abre
} // fim do construtor

    // ===================== MÉTODO CONECTA =====================
    // static = pode ser usado sem criar janela: Geoprocessamento.conecta()
    // Devolve a conexão com o banco, ou null se der erro.
   static Connection conecta() {
        String url = "jdbc:mysql://localhost:3306/geoprocessamento"; // localhost = este computador, 3306 = porta do MySQL, geoprocessamento = nome do banco
        Connection con; // variável da conexão

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");               // carrega o driver do MySQL (vem do pom.xml)
            con = DriverManager.getConnection(url, "root", "");      // abre a conexão: usuário root, senha vazia
            return con;                                              // devolve a conexão aberta

        } catch (ClassNotFoundException cnf) { // erro: driver não encontrado
            System.out.println("Erro no driver: " + cnf);
            return null;

        } catch (SQLException sql) {           // erro de SQL (ex.: MySQL desligado ou banco não existe)
            System.out.println("Erro no SQL: " + sql);
            return null;
        }
    }


    // ===================== MÉTODO CARREGAR IMAGENS =====================
    // Busca os registros salvos no banco e mostra na tabela
    void carregarImagens() {
        // a coluna "imagem" fica de fora de propósito, para carregar mais rápido
        String sql = "SELECT id, data_cadastro, hora_cadastro, uf, nome_imagem "
                   + "FROM cadastro_imagem";

        try (Connection con = conecta();                                // abre a conexão
             java.sql.PreparedStatement stmt = con.prepareStatement(sql); // prepara o comando
             java.sql.ResultSet rs = stmt.executeQuery()) {             // executa; rs (ResultSet) guarda as linhas que vieram do banco

            RegistroImagens.setRowCount(0); // limpa a tabela antes de preencher

            while (rs.next()) { // next() vai para a próxima linha do resultado, enquanto existir
                RegistroImagens.addRow(new Object[]{ // adiciona uma linha na tabela com os 5 valores
                    rs.getInt("id"),              // pega cada valor pelo nome da coluna no banco
                    rs.getDate("data_cadastro"),
                    rs.getTime("hora_cadastro"),
                    rs.getString("uf"),
                    rs.getString("nome_imagem")
                });
            }

        } catch (Exception erro) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar imagens: " + erro.getMessage());
        }
    }

    // ===================== MÉTODO DESORDENAR =====================
    // Embaralha as linhas da tabela de forma aleatória
    void desordenarTabela() {
        java.util.List<Object[]> linhas = new java.util.ArrayList<>(); // lista que cresce à vontade (ArrayList)

        int totalLinhas = RegistroImagens.getRowCount();    // quantas linhas
        int totalColunas = RegistroImagens.getColumnCount(); // quantas colunas

        for (int i = 0; i < totalLinhas; i++) {          // para cada linha...
            Object[] linha = new Object[totalColunas];   // ...cria um vetor
            for (int c = 0; c < totalColunas; c++) {     // para cada coluna...
                linha[c] = RegistroImagens.getValueAt(i, c);
                // pega o valor da linha i, coluna c
            }
            linhas.add(linha);                           // coloca o vetor na lista
        }

        java.util.Collections.shuffle(linhas); // EMBARALHA a lista aleatoriamente (é aqui que desordena)

        RegistroImagens.setRowCount(0);      // esvazia a tabela

        for (Object[] linha : linhas) {      // devolve as linhas embaralhadas para a tabela
            RegistroImagens.addRow(linha);// qual método adiciona uma linha nova?
        }
    }

    // ===================== MÉTODO VER IMAGEM =====================
    // Busca a imagem do registro no banco e mostra numa janelinha
    void verImagem(String id, String nome) {
        String sql = "SELECT imagem FROM cadastro_imagem WHERE id = ?"; // busca só a imagem daquele ID

        try (Connection con = conecta();
             java.sql.PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, Integer.parseInt(id)); // coloca o ID no lugar do ?

            try (java.sql.ResultSet rs = stmt.executeQuery()) { // executa a busca
                if (!rs.next()) { // se não veio nenhum resultado...
                    return;       // ...para
                }

                byte[] bytes = rs.getBytes("imagem"); // pega a imagem em bytes (dados brutos do arquivo)

                if (bytes == null) { // registro sem imagem
                    JOptionPane.showMessageDialog(this, "Este registro ainda não tem imagem.");
                    return;
                }

                ImageIcon icone = new ImageIcon(bytes); // transforma os bytes numa imagem que o Java mostra na tela
                int larg = icone.getIconWidth();        // largura da imagem
                int alt = icone.getIconHeight();        // altura da imagem

                if (larg <= 0) { // largura 0 ou menor = não é uma imagem válida
                    JOptionPane.showMessageDialog(this, "Não foi possível ler essa imagem.");
                    return;
                }

                // se for maior que 800x600, reduz MANTENDO A PROPORÇÃO
                if (larg > 800 || alt > 600) { // || = "ou"
                    double fator = Math.min(800.0 / larg, 600.0 / alt); // Math.min pega o MENOR fator, para caber nos dois sentidos
                    Image reduzida = icone.getImage().getScaledInstance(
                            (int) (larg * fator), (int) (alt * fator), Image.SCALE_SMOOTH); // (int) tira as casas decimais; SCALE_SMOOTH = boa qualidade
                    icone = new ImageIcon(reduzida); // troca pela imagem reduzida
                }

                JDialog janela = new JDialog(this, nome, true); // janelinha com o nome no título; true = MODAL (trava a tela de trás)
                janela.add(new JLabel(icone));                  // coloca a imagem dentro
                janela.pack();                                  // ajusta o tamanho da janela ao da imagem
                janela.setLocationRelativeTo(this);             // centraliza sobre a tela principal
                janela.setVisible(true);                        // mostra
            }

        } catch (Exception erro) {
            JOptionPane.showMessageDialog(this, "Erro ao abrir a imagem: " + erro.getMessage());
        }
    }



    // ===================== MAIN =====================
    // Ponto de partida do programa: é por aqui que o Java começa
    public static void main(String[] args) {
      Geoprocessamento tela= new Geoprocessamento(); // cria a tela principal (roda o construtor)
      tela.setVisible(true);                         // deixa a janela visível

   }



 } // fim da classe Geoprocessamento




