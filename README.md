# Geoprocessamento

Sistema desktop em **Java (Swing)** com banco de dados **MySQL** para cadastrar, editar, excluir e visualizar imagens de geoprocessamento, com data, hora e UF. O sistema também compara o tempo de ordenação de três algoritmos: **Bubble Sort**, **Insertion Sort** e **Quick Sort**.

Trabalho semestral desenvolvido em grupo.

## Funcionalidades

- **Cadastrar** uma imagem com data, hora, UF e nome. A imagem fica salva no próprio banco.
- **Editar** um registro, com ou sem trocar a imagem.
- **Excluir** um registro, depois de confirmar.
- **Ver imagem** numa janela. Imagens maiores que 800×600 são reduzidas sem perder a proporção.
- **Desordenar** a tabela de forma aleatória.
- **Ordenar** os registros por ID com um ou mais algoritmos e ver o tempo de cada um, em milissegundos.

## Tecnologias

| Tecnologia | Uso |
| --- | --- |
| Java 25 | Linguagem do projeto |
| Swing | Interface gráfica (janelas, tabelas, botões) |
| MySQL | Banco de dados |
| JDBC + mysql-connector-j 9.5.0 | Conexão do Java com o MySQL |
| Maven | Compilação e dependências |

## Estrutura do projeto

```
src/main/java/geoprocessamento/
├── Geoprocessamento.java     # Tela principal, conexão com o banco, botões e ordenação
├── CadastroImagem.java       # Formulário de cadastro e edição
├── MetodosDeOrdenacao.java   # Bubble Sort, Insertion Sort e Quick Sort
└── CarregadorDeImagens.java  # Programa à parte que insere várias fotos de uma vez no banco
```

## Algoritmos de ordenação

| Algoritmo | Ideia | Complexidade |
| --- | --- | --- |
| Bubble Sort | Compara vizinhos e troca os que estão fora de ordem | O(n²) |
| Insertion Sort | Encaixa cada elemento no lugar certo entre os anteriores | O(n²), ou O(n) com os dados quase ordenados |
| Quick Sort | Usa o elemento do meio como pivô e separa os menores dos maiores, de forma recursiva | O(n log n) em média |

Cada algoritmo recebe uma **cópia** dos mesmos dados desordenados, pra comparação ser justa. O tempo é medido com `System.nanoTime()`.

## Como rodar

### 1. Criar o banco

No MySQL, execute:

```sql
CREATE DATABASE geoprocessamento;
USE geoprocessamento;

CREATE TABLE cadastro_imagem (
    id INT AUTO_INCREMENT PRIMARY KEY,
    data_cadastro DATE NOT NULL,
    hora_cadastro TIME NOT NULL,
    uf CHAR(2) NOT NULL,
    nome_imagem VARCHAR(255) NOT NULL,
    imagem LONGBLOB
);
```

### 2. Configurar a conexão

A conexão fica no método `conecta()`, em `Geoprocessamento.java`. Por padrão ela usa:

- endereço `jdbc:mysql://localhost:3306/geoprocessamento`
- usuário `root`
- senha vazia

Se o seu MySQL usar outro usuário ou outra senha, altere essas informações.

### 3. Executar

Abra o projeto numa IDE com suporte a Maven, como NetBeans ou IntelliJ, e rode a classe `geoprocessamento.Geoprocessamento`.

Pelo terminal:

```bash
mvn compile exec:java
```

### 4. (Opcional) Gerar a massa de dados

O `CarregadorDeImagens` insere no banco todas as fotos `.jpg` de uma pasta, com data, hora e UF sorteadas. Antes de rodar, troque o caminho da pasta na linha:

```java
java.io.File pasta = new java.io.File("C:/caminho/da/sua/pasta");
```

Depois, rode a classe `geoprocessamento.CarregadorDeImagens`.

## Como usar

1. Clique em **Cadastrar**, preencha a data (`dd/MM/aaaa`), a hora (`HH:mm`), a UF e escolha uma imagem.
2. Clique em **Desordenar** pra embaralhar a tabela.
3. Marque um ou mais algoritmos e clique em **Ordenar**.
4. Compare os tempos no quadro **Tempo de Ordenação**.
