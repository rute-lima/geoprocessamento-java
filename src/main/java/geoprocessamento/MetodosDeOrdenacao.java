package geoprocessamento; // esta classe faz parte do pacote geoprocessamento (mesma pasta das outras classes)

/**
 * Classe que guarda os três métodos de ordenação do projeto:
 * Bubble Sort, Insertion Sort e Quick Sort.
 *
 * Todos recebem uma matriz Object[][] em que cada linha é um registro da tabela
 * (ID, Data, Hora, UF, Nome) e ordenam pela COLUNA 0, que é o ID, do menor para o maior.
 * Quando é preciso trocar, eles trocam a LINHA INTEIRA de lugar, e não só o ID.
 *
 * Os métodos são "public static": podem ser chamados direto pelo nome da classe,
 * sem criar um objeto. Exemplo: MetodosDeOrdenacao.bubbleSort(copia);
 *
 * @author ruter
 */
public class MetodosDeOrdenacao {

    // ===================================================================
    // BUBBLE SORT - complexidade O(n²), o mais lento dos três
    // Ideia: compara cada elemento com o vizinho da direita e troca se
    // estiver fora de ordem. A cada volta, o maior "borbulha" para o final.
    // ===================================================================
    public static void bubbleSort(Object[][] v) {
        int n = v.length; // n = quantidade de linhas (registros) da matriz
        for (int i = 0; i < n; i++) { // laço de fora: cada repetição é uma "volta" completa pela matriz
            for (int j = 0; j < n - 1 - i; j++) { // laço de dentro: compara a posição j com a vizinha j+1; o "- i" ignora o final, que já está ordenado
                // (Integer) é um cast: diz ao Java que o valor da coluna 0 é um número inteiro
                // compareTo devolve > 0 quando o primeiro é maior que o segundo
                if (((Integer) v[j][0]).compareTo((Integer) v[j + 1][0]) > 0) { // se o ID da posição j for maior que o da próxima...
                    Object[] aux = v[j];   // ...guarda a linha j numa variável auxiliar
                    v[j] = v[j + 1];       // coloca a linha da direita no lugar da j
                    v[j + 1] = aux;        // coloca a linha guardada na posição da direita (troca concluída)
                }
            }
        }
    }







    // ===================================================================
    // INSERTION SORT - complexidade O(n²); O(n) se os dados já estiverem quase ordenados
    // Ideia: pega um elemento e "encaixa" ele no lugar certo entre os anteriores,
    // igual a organizar cartas na mão.
    // ===================================================================
    public static void insertionSort(Object[][] v) {
        int n = v.length; // quantidade de linhas
        for (int i = 1; i < n; i++) { // começa do segundo elemento (i = 1), porque um elemento sozinho já está ordenado
            Object[] aux = v[i]; // guarda a linha que vai ser encaixada no lugar certo
            int j = i - 1;       // j começa na posição anterior a i
            // enquanto j não passou do começo (j >= 0) E o ID em j for maior que o ID do aux...
            while (j >= 0 && ((Integer) v[j][0]).compareTo((Integer) aux[0]) > 0) {
                v[j + 1] = v[j]; // ...empurra a linha j uma posição para a direita
                j--;             // e anda uma posição para a esquerda
            }
            v[j + 1] = aux; // coloca o aux no "buraco" que sobrou, que é o lugar certo dele
        }
    }

    // ===================================================================
    // QUICK SORT - complexidade O(n log n) em média, o mais rápido dos três
    // Ideia: escolhe um PIVÔ, coloca os menores que ele à esquerda e os maiores
    // à direita, e repete o processo em cada lado (recursão).
    // ===================================================================

    // Método público: é o que a tela chama. Ele só chama a versão completa
    // passando o início (0) e o fim (v.length - 1) da matriz.
    // Dois métodos com o mesmo nome e parâmetros diferentes = SOBRECARGA.
    public static void quickSort(Object[][] v) {
        quickSort(v, 0, v.length - 1);
    }






    // Versão completa (private: só esta classe usa). Ordena o pedaço da matriz
    // que vai da posição "esquerda" até a posição "direita".
    private static void quickSort(Object[][] v, int esquerda, int direita) {
        int esq = esquerda; // ponteiro que anda da esquerda para a direita
        int dir = direita;  // ponteiro que anda da direita para a esquerda
        Integer pivo = (Integer) v[(esq + dir) / 2][0]; // o pivô é o ID do elemento do MEIO do pedaço
        Object[] troca;     // variável auxiliar para trocar duas linhas

        while (esq <= dir) { // repete enquanto os dois ponteiros não se cruzarem
            while (((Integer) v[esq][0]).compareTo(pivo) < 0) { // enquanto o valor da esquerda for MENOR que o pivô (já está do lado certo)...
                esq = esq + 1;                                   // ...avança o ponteiro da esquerda
            }
            while (((Integer) v[dir][0]).compareTo(pivo) > 0) { // enquanto o valor da direita for MAIOR que o pivô (já está do lado certo)...
                dir = dir - 1;                                   // ...recua o ponteiro da direita
            }
            if (esq <= dir) {      // os dois pararam em elementos do lado errado: troca eles
                troca = v[esq];    // guarda a linha da esquerda
                v[esq] = v[dir];   // coloca a da direita na esquerda
                v[dir] = troca;    // coloca a guardada na direita
                esq = esq + 1;     // avança os dois ponteiros
                dir = dir - 1;
            }
        }
        if (dir > esquerda) {             // se sobrou um pedaço à esquerda para ordenar...
            quickSort(v, esquerda, dir);  // ...o método chama ELE MESMO para esse pedaço (RECURSÃO)
        }
        if (esq < direita) {              // se sobrou um pedaço à direita para ordenar...
            quickSort(v, esq, direita);   // ...chama ele mesmo para esse pedaço
        }
    }
}
