package geoprocessamento; // mesmo pacote das outras classes

import javax.swing.*; // importa janelas, botões e campos de texto
import java.awt.Color; //permite definir as cores
import java.awt.Font; //permite personalizar as  fontes
import java.io.File; //permite trabalhar com arquivos do computador
import java.io.FileInputStream; //permite ler o conteudo de um arquivo
import java.sql.Connection; // conexão com o banco
import java.sql.PreparedStatement; // comando SQL com ? que são preenchidos depois (protege contra SQL Injection)
import java.sql.SQLException; // tipo de erro de SQL (importado, mas não usado diretamente)
import java.sql.Date; //permite trabalhar com data
import java.sql.Time; //permite trablhar com horas
import java.sql.Timestamp; // data + hora juntas (importado, mas não usado)
import java.time.LocalDate; // data do Java moderno (dia, mês, ano)
import java.time.LocalTime; // hora do Java moderno (hora, minuto, segundo)

/**
 * TELA DO FORMULÁRIO. A mesma tela serve para CADASTRAR um registro novo
 * e para EDITAR um registro existente.
 */
public class CadastroImagem extends JFrame{ //Jframe é a classe que representa uma janela, ou seja cadastro imagem esa herdando os recursos de uma jenala

    // ATRIBUTOS
    private JTextField txtID; //variavel que guarda oq foi digitada em id
    private JTextField txtData;        // campo da data
    private JTextField txtHora;        // campo da hora
    private JTextField txtUF;          // campo da UF
    private JTextField txtNomeImagem;  // campo do nome da imagem
    private JLabel lblVisualizacao;    // "quadro" onde a imagem aparece
    private File arquivoImagem;        // arquivo da imagem escolhida (null se nenhuma foi escolhida)

    // CONSTRUTOR 1 (sem parâmetros) -> usado pelo botão CADASTRAR
    public CadastroImagem() { //construtor sem pÂrametros, usado para abrir um novo cadastro nvo
        this(null, null, null, null, null); //chama o construtor sem valores nele
    }

    // CONSTRUTOR 2 (com 5 parâmetros) -> usado pelo botão EDITAR
    // Dois construtores com parâmetros diferentes = SOBRECARGA
    public CadastroImagem(String id, String data, String hora, String uf, String nomeImagem) { //construtor que recebe os dados de um registro

        setTitle ("Geoprocessamento"); //define o titulo que aparece no topo da janela
        setSize(700,600); //define as dimensões da janela
        setLocationRelativeTo(null); //posiciona a janela no centro da tela
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //setDefaultCloseOperation() define o comportamento da janela quando clicamos no botão X // JFrame.DISPOSE_ON_CLOSE fecha e libera os recursos dessa janela, sem encerrar obrigatoriamente todo o programa

        JPanel painelPrincipal = new JPanel(); //JPanel é a classe Swing que representa um painel // painelPrincipal é o nome da variável que guardará nosso painel //new JPanel()cria um novo objeto de painel.
        painelPrincipal.setBackground(Color.WHITE); //define a cor de fundo
        painelPrincipal.setLayout(null); //setLayout()define como os componentes serão posicionados dentro do painel o null significa que não vai ser posicionado aumtomaticamente

        // ---------- título ----------
        JLabel lblTitulo = new JLabel("Cadastro de imagem"); //define a variavel lbltitulo que exibe o titulo geoprocessamento
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 26)); //define o formato do texto //Arial define o tipo da fonte //Font.BOLD deixa o texto em negrito // 26 define o tamanho da fonte
        lblTitulo.setForeground(new Color(36, 107, 69)); //setForeground()define a cor do texto
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER); // centraliza o texto
        lblTitulo.setBounds(0, 20, 700, 35); //define o tamanho e a posição
        painelPrincipal.add(lblTitulo); //adicona o titulo ao pailnel

        // ---------- ID ----------
        // padrão que se repete em todos os campos: cria o rótulo (JLabel), define fonte,
        // posiciona com setBounds(x, y, largura, altura) e adiciona ao painel; depois o mesmo para o campo (JTextField)
        JLabel lblID = new JLabel("ID:");                 // rótulo (texto fixo)
        lblID.setFont(new Font("Arial", Font.PLAIN, 14)); // Arial, PLAIN = sem negrito, tamanho 14
        lblID.setBounds(25, 75, 100, 25);                 // x=25, y=75, largura=100, altura=25
        painelPrincipal.add(lblID);                       // adiciona ao painel (sem isso não aparece)

        txtID = new JTextField();                         // campo de texto
        txtID.setBounds(25, 100, 200, 30);                // posição logo abaixo do rótulo
        txtID.setEditable(false); //impede que o úsuario digite o id
        painelPrincipal.add(txtID);

        // ---------- Data ----------
        JLabel lblData = new JLabel("Data:");
        lblData.setFont(new Font("Arial", Font.PLAIN, 14));
        lblData.setBounds(25, 145, 100, 25);
        painelPrincipal.add(lblData);

        txtData = new JTextField(); // o usuário digita no formato dd/MM/aaaa
        txtData.setBounds(25, 170, 200, 30);
        painelPrincipal.add(txtData);

        // ---------- Hora (70 pixels abaixo da Data) ----------
        JLabel lblHora = new JLabel("Hora:");
        lblHora.setFont(new Font("Arial", Font.PLAIN, 14));
        lblHora.setBounds(25, 215, 100, 25);
        painelPrincipal.add(lblHora);

        txtHora = new JTextField(); // o usuário digita no formato HH:mm
        txtHora.setBounds(25, 240, 200, 30);
        painelPrincipal.add(txtHora);

        // ---------- UF ----------
        JLabel lblUF = new JLabel("UF:");
        lblUF.setFont(new Font("Arial", Font.PLAIN, 14));
        lblUF.setBounds(25, 285, 100, 25);
        painelPrincipal.add(lblUF);

        txtUF = new JTextField();
        txtUF.setBounds(25, 310, 200, 30);
        painelPrincipal.add(txtUF);

        // ---------- Nome da imagem (mais largo, para caber nomes compridos) ----------
        JLabel lblNomeImagem = new JLabel("Nome da imagem:");
        lblNomeImagem.setFont(new Font("Arial", Font.PLAIN, 14));
        lblNomeImagem.setBounds(25, 355, 200, 25);
        painelPrincipal.add(lblNomeImagem);

        txtNomeImagem = new JTextField();
        txtNomeImagem.setBounds(25, 380, 300, 30);
        painelPrincipal.add(txtNomeImagem);
        //cria o campo onde sera exibido a imagem
        lblVisualizacao = new JLabel("Nenhuma imagem selecionada"); // um JLabel usado como "quadro" da imagem
        lblVisualizacao.setBounds(350, 100, 280, 250);              // à direita dos campos, 280 x 250
        lblVisualizacao.setBorder( //adicona uma borda cinza onde esta a area que a imagem aparece
        BorderFactory.createLineBorder(Color.GRAY));
        lblVisualizacao.setHorizontalAlignment(SwingConstants.CENTER); //centraliza o texto e a iamgem dentro do quadrado cinza
        painelPrincipal.add(lblVisualizacao);

        //cria o botao selecionar imagem
        JButton btnSelecionarImagem =
        new JButton("Selecionar imagem");
        btnSelecionarImagem.setBounds(350, 380, 170, 30); // embaixo do quadro
        painelPrincipal.add(btnSelecionarImagem);

        //Botão Salvar
        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.setBounds(350, 470, 100, 35);
        btnSalvar.setBackground(new Color(0, 102, 153)); // fundo azul (cor em RGB: vermelho, verde, azul)
        btnSalvar.setForeground(Color.WHITE);            // texto branco
        painelPrincipal.add(btnSalvar);

        //Botão Cancelar
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBounds(465, 470, 100, 35); // ao lado do Salvar
        painelPrincipal.add(btnCancelar);

        //Adiciona o painel principal a janela
        add(painelPrincipal);

        // ---------- só na EDIÇÃO: preenche os campos com os dados recebidos ----------
        if (id != null) { //verifica se os dados que foram recebidos existem
            txtID.setText(id); //coloca oq foi preenchido dentro do campo id
            // a data vem do banco como ano-mês-dia (2023-05-10); parse transforma em data e format reescreve como 10/05/2023
            txtData.setText(java.time.LocalDate.parse(data)
                    .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            txtHora.setText(hora);             // preenche a hora
            txtUF.setText(uf);                 // preenche a UF
            txtNomeImagem.setText(nomeImagem); // preenche o nome
            carregarImagemDoBanco(id);         // busca a imagem no banco e mostra no quadro
        }

        // ===================== BOTÃO SALVAR =====================
        // Ação do botão Salvar
        btnSalvar.addActionListener(e -> { //o actionlister percebe quando e acionado e define oq vai fazer quando for acionado
            try { //tipo if e else
                boolean ehEdicao = !txtID.getText().trim().isEmpty(); // se o campo ID estiver preenchido, é EDIÇÃO; se vazio, é CADASTRO
                if (txtData.getText().trim().isEmpty() //verifica se nenhum campo obrigatorio esta vazio //.trim()  remove os espaços em branco no começo e final //.isEmpty()verifica se o campo esta vazio
                        || txtHora.getText().trim().isEmpty()// ||significa OU //se uma das condiçoes for verdadeiras entra no if
                        || txtUF.getText().trim().isEmpty()
                        || txtNomeImagem.getText().trim().isEmpty()
                        || (!ehEdicao && arquivoImagem == null)) { //verifica se o campo da imagem esta vazio

                    JOptionPane.showMessageDialog(this, "Preencha os campos e selecione uma imagem.");
                    return; //interrompe a ação sem que o programa feche
                }

                java.time.format.DateTimeFormatter formatoData = //uma classe do java que formata e interpreta datas e horarios
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"); //cria um formato personalizado para data

                java.time.LocalDate dataConvertida = java.time.LocalDate.parse( // essa linha pega oq foi digitado em data e transforma em uma data que o java reconhece
                    txtData.getText().trim(), formatoData);

                java.time.LocalTime horaConvertida = java.time.LocalTime.parse( //converte i texto em horario
                    txtHora.getText().trim());
                //cria um comando sql para ser adiconado no banco oq foi digitado no banco
                String sql = "INSERT INTO cadastro_imagem " + "(data_cadastro, hora_cadastro, uf, nome_imagem, imagem) " + "VALUES (?, ?, ?, ?, ?)";


                // as ?? indica os cinco valores que serão preenchidos pelo Java. Cada ? corresponde a uma coluna, na mesma ordem em que elas foram declaradas.

                // ----- CAMINHO 1: CADASTRO NOVO -> INSERT -----
                if (!ehEdicao) {
                    // try-with-resources: abre conexão, prepara o comando e abre o arquivo; fecha os três sozinho no final
                    try (java.sql.Connection con = Geoprocessamento.conecta();
                         java.sql.PreparedStatement stmt = con.prepareStatement(sql);
                         java.io.FileInputStream arquivo =
                                 new java.io.FileInputStream(arquivoImagem)) { // FileInputStream lê o conteúdo da imagem

                        stmt.setDate(1, java.sql.Date.valueOf(dataConvertida)); //Define a data que será colocada na primeira interrogação.
                        stmt.setTime(2, java.sql.Time.valueOf(horaConvertida)); // 2º ? = hora
                        stmt.setString(3, txtUF.getText().trim().toUpperCase()); // 3º ? = UF em maiúsculas (toUpperCase)
                        stmt.setString(4, txtNomeImagem.getText().trim());       // 4º ? = nome
                        stmt.setBinaryStream(5, arquivo, arquivoImagem.length()); // 5º ? = a imagem em binário (BLOB)

                        stmt.executeUpdate(); // executa o INSERT (grava no banco)

                        JOptionPane.showMessageDialog(this, "Imagem salva com sucesso!");
                        dispose(); // fecha a janela; isso faz a tela principal recarregar a tabela
                    }
                // ----- CAMINHO 2: EDIÇÃO SEM TROCAR A IMAGEM -> UPDATE sem a coluna imagem -----
                } else if(arquivoImagem == null) {
                    String SqlUpdate = "UPDATE cadastro_imagem SET data_cadastro =?, hora_cadastro=?, uf =?, nome_imagem=? WHERE id =?";
                    try (java.sql.Connection con = Geoprocessamento.conecta();
                         java.sql.PreparedStatement stmt = con.prepareStatement(SqlUpdate)){

                        stmt.setDate(1, java.sql.Date.valueOf(dataConvertida));  // 1º ? = data
                        stmt.setTime(2, java.sql.Time.valueOf(horaConvertida));  // 2º ? = hora
                        stmt.setString(3, txtUF.getText().trim().toUpperCase()); // 3º ? = UF
                        stmt.setString(4, txtNomeImagem.getText().trim());       // 4º ? = nome
                        stmt.setInt(5, Integer.parseInt(txtID.getText().trim())); // 5º ? = ID (qual registro alterar)

                        stmt.executeUpdate(); // executa o UPDATE (a imagem antiga continua no banco)
                        JOptionPane.showMessageDialog(this, "Imagem atualizada com sucesso!");
                        dispose();

                    }

                }
                // ----- CAMINHO 3: EDIÇÃO TROCANDO A IMAGEM -> UPDATE com a coluna imagem -----
                else{
                    String sqlUpdateImagem = "UPDATE cadastro_imagem SET data_cadastro =?, hora_cadastro=?, uf =?, nome_imagem=?, imagem=? WHERE id =?";
                    try (java.sql.Connection con = Geoprocessamento.conecta();
                         java.sql.PreparedStatement stmt = con.prepareStatement(sqlUpdateImagem);
                         java.io.FileInputStream arquivo =
                                 new java.io.FileInputStream(arquivoImagem)) {
                        stmt.setDate(1, java.sql.Date.valueOf(dataConvertida));
                        stmt.setTime(2, java.sql.Time.valueOf(horaConvertida));
                        stmt.setString(3, txtUF.getText().trim().toUpperCase());
                        stmt.setString(4, txtNomeImagem.getText().trim());
                        stmt.setBinaryStream(5, arquivo, arquivoImagem.length()); // 5º ? = imagem NOVA
                        stmt.setInt(6, Integer.parseInt(txtID.getText().trim())); // 6º ? = ID

                        stmt.executeUpdate();
                        JOptionPane.showMessageDialog(this, "Imagem atualizada com sucesso!");
                        dispose();

                    }
                }

            } catch (Exception erro) { // qualquer erro (ex.: data digitada no formato errado) cai aqui
                JOptionPane.showMessageDialog(this,
                        "Erro ao salvar: " + erro.getMessage());
            }
        });

        // Ação do botão Cancelar
        btnCancelar.addActionListener(e -> dispose()); // fecha a janela sem salvar

        // ===================== BOTÃO SELECIONAR IMAGEM =====================
        // Ação do botão Selecionar imagem
        btnSelecionarImagem.addActionListener(e -> {
    JFileChooser seletor = new JFileChooser(); // explorador de arquivos para escolher a imagem

    int resultado = seletor.showOpenDialog(this); // abre a janela de escolher e guarda o que o usuário fez

    if (resultado == JFileChooser.APPROVE_OPTION) { // APPROVE_OPTION = o usuário escolheu um arquivo (clicou em Abrir)
        arquivoImagem = seletor.getSelectedFile(); // guarda o arquivo escolhido

        txtNomeImagem.setText(arquivoImagem.getName()); // coloca o nome do arquivo no campo Nome

        ImageIcon imagem = new ImageIcon(arquivoImagem.getAbsolutePath()); // carrega a imagem pelo caminho do arquivo

        // redimensiona a imagem para o tamanho do quadro
        java.awt.Image imagemRedimensionada =
            imagem.getImage().getScaledInstance(
                lblVisualizacao.getWidth(),   // largura do quadro
                lblVisualizacao.getHeight(),  // altura do quadro
                java.awt.Image.SCALE_SMOOTH   // boa qualidade
            );

        lblVisualizacao.setText(""); // apaga o texto "Nenhuma imagem selecionada"
        lblVisualizacao.setIcon(     // mostra a imagem no quadro
            new ImageIcon(imagemRedimensionada)
        );
    }
});
} // fim do construtor

    // ===================== MÉTODO CARREGAR IMAGEM DO BANCO =====================
    // Busca a imagem do registro no banco e mostra no quadro (usado na EDIÇÃO)
    private void carregarImagemDoBanco(String id) {
        String sql = "SELECT imagem FROM cadastro_imagem WHERE id = ?"; // busca só a imagem daquele ID

        try (Connection con = Geoprocessamento.conecta();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, Integer.parseInt(id));          // coloca o ID no lugar do ?
            java.sql.ResultSet resultado = stmt.executeQuery(); // executa a busca

            if (resultado.next()) { // se veio resultado...
                byte[] bytesImagem = resultado.getBytes("imagem"); // pega a imagem em bytes

                ImageIcon imagem = new ImageIcon(bytesImagem); // transforma em imagem

                // redimensiona para o tamanho do quadro
                java.awt.Image imagemRedimensionada =
                        imagem.getImage().getScaledInstance(
                                lblVisualizacao.getWidth(),
                                lblVisualizacao.getHeight(),
                                java.awt.Image.SCALE_SMOOTH
                        );

                lblVisualizacao.setText("");                                // apaga o texto
                lblVisualizacao.setIcon(new ImageIcon(imagemRedimensionada)); // mostra a imagem
            }

        } catch (Exception erro) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar imagem: " + erro.getMessage());
        }
    }



    //Método principal para abrir a tela
    // (serve para testar só esta tela; no uso normal, quem abre o formulário é a tela principal)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> { // abre a janela na thread certa do Swing
            new CadastroImagem().setVisible(true);
        });
    }
}
