import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class DocumentService {

    Document document;

    public DocumentService(String filepath) {
        this.document = new Document();
        this.document.setDocumentContent(loadDocument(filepath));
    }

    // Wczytanie dokumentu
    private String loadDocument(String filePath) {
        File file = new File(filePath);
        StringBuilder documentBuilder = new StringBuilder();

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                documentBuilder.append(line).append("\n");
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return documentBuilder.toString();
    }

    // Procesowanie wszystkiego - odpalanie funkcji
    public String process(int i) {
        List<String> preprocessedDocument = preprocessDocument();
        Map<String, Integer> tf = calculateTermFrequency(preprocessedDocument);
        Map<String, Double> idf = calculateInverseDocumentFrequency(tf.keySet());
        Map<String, Double> tfidf = calculateTFIDF(tf, idf);
        return extractKeywords(tfidf, i);
    }

    // Podzielenie tekstu na słowa i wykluczenie stop wordsów
    public List<String> preprocessDocument() {
        String[] tokens = document.getDocumentContent().toLowerCase().split(" ");
        List<String> result = new ArrayList<>();

        for (String token : tokens) {
            if (!STOP_WORDS.contains(token)) {
                result.add(token);
            }
        }

        return result;
    }

    // https://github.com/bieli/stopwords/blob/master/polish.stopwords.txt
    private static final Set<String> STOP_WORDS = new HashSet<>(
            Arrays.asList("a", "aby", "ach", "acz", "aczkolwiek", "aj", "albo", "ale", "alez", "ależ", "ani", "az", "aż", "bardziej", "bardzo", "beda", "bedzie", "bez", "deda", "będą", "bede", "będę", "będzie", "bo", "bowiem", "by", "byc", "być", "byl", "byla", "byli", "bylo", "byly", "był", "była", "było", "były", "bynajmniej", "cala", "cali", "caly", "cała", "cały", "ci", "cie", "ciebie", "cię", "co", "cokolwiek", "cos", "coś", "czasami", "czasem", "czemu", "czy", "czyli", "daleko", "dla", "dlaczego", "dlatego", "do", "dobrze", "dokad", "dokąd", "dosc", "dość", "duzo", "dużo", "dwa", "dwaj", "dwie", "dwoje", "dzis", "dzisiaj", "dziś", "gdy", "gdyby", "gdyz", "gdyż", "gdzie", "gdziekolwiek", "gdzies", "gdzieś", "go", "i", "ich", "ile", "im", "inna", "inne", "inny", "innych", "iz", "iż", "ja", "jak", "jakas", "jakaś", "jakby", "jaki", "jakichs", "jakichś", "jakie", "jakis", "jakiś", "jakiz", "jakiż", "jakkolwiek", "jako", "jakos", "jakoś", "ją", "je", "jeden", "jedna", "jednak", "jednakze", "jednakże", "jedno", "jego", "jej", "jemu", "jesli", "jest", "jestem", "jeszcze", "jeśli", "jezeli", "jeżeli", "juz", "już", "kazdy", "każdy", "kiedy", "kilka", "kims", "kimś", "kto", "ktokolwiek", "ktora", "ktore", "ktorego", "ktorej", "ktory", "ktorych", "ktorym", "ktorzy", "ktos", "ktoś", "która", "które", "którego", "której", "który", "których", "którym", "którzy", "ku", "lat", "lecz", "lub", "ma", "mają", "mało", "mam", "mi", "miedzy", "między", "mimo", "mna", "mną", "mnie", "moga", "mogą", "moi", "moim", "moj", "moja", "moje", "moze", "mozliwe", "mozna", "może", "możliwe", "można", "mój", "mu", "musi", "my", "na", "nad", "nam", "nami", "nas", "nasi", "nasz", "nasza", "nasze", "naszego", "naszych", "natomiast", "natychmiast", "nawet", "nia", "nią", "nic", "nich", "nie", "niech", "niego", "niej", "niemu", "nigdy", "nim", "nimi", "niz", "niż", "no", "o", "obok", "od", "około", "on", "ona", "one", "oni", "ono", "oraz", "oto", "owszem", "pan", "pana", "pani", "po", "pod", "podczas", "pomimo", "ponad", "poniewaz", "ponieważ", "powinien", "powinna", "powinni", "powinno", "poza", "prawie", "przeciez", "przecież", "przed", "przede", "przedtem", "przez", "przy", "roku", "rowniez", "również", "sam", "sama", "są", "sie", "się", "skad", "skąd", "soba", "sobą", "sobie", "sposob", "sposób", "swoje", "ta", "tak", "taka", "taki", "takie", "takze", "także", "tam", "te", "tego", "tej", "ten", "teraz", "też", "to", "toba", "tobą", "tobie", "totez", "toteż", "totobą", "trzeba", "tu", "tutaj", "twoi", "twoim", "twoj", "twoja", "twoje", "twój", "twym", "ty", "tych", "tylko", "tym", "u", "w", "wam", "wami", "was", "wasz", "wasza", "wasze", "we", "według", "wiele", "wielu", "więc", "więcej", "wlasnie", "właśnie", "wszyscy", "wszystkich", "wszystkie", "wszystkim", "wszystko", "wtedy", "wy", "z", "za", "zaden", "zadna", "zadne", "zadnych", "zapewne", "zawsze", "ze", "zeby", "zeznowu", "zł", "znow", "znowu", "znów", "zostal", "został", "żaden", "żadna", "żadne", "żadnych", "że", "żeby")
    );

    // calculateTermFrequency
    public Map<String, Integer> calculateTermFrequency(List<String> tokens) {
        Map<String, Integer> termFrequency = new HashMap<>();

        for (String token : tokens) {
            termFrequency.put(token, termFrequency.getOrDefault(token, 0) + 1);
        }

        return termFrequency;
    }

    // calculateInverseDocumentFrequency
    public Map<String, Double> calculateInverseDocumentFrequency(Set<String>... documentSets) {
        Map<String, Integer> documentFrequency = new HashMap<>();

        for (Set<String> documentSet : documentSets) {
            for (String term : documentSet) {
                documentFrequency.put(term, documentFrequency.getOrDefault(term, 0) + 1);
            }
        }

        Map<String, Double> inverseDocumentFrequency = new HashMap<>();
        int totalDocuments = documentSets.length;

        for (Map.Entry<String, Integer> entry : documentFrequency.entrySet()) {
            String term = entry.getKey();
            int documentCount = entry.getValue();
            double idf = Math.log((double) totalDocuments / (documentCount + 1));
            inverseDocumentFrequency.put(term, idf);
        }

        return inverseDocumentFrequency;
    }

    // calculateTFIDF
    public Map<String, Double> calculateTFIDF(Map<String, Integer> termFrequency, Map<String, Double> inverseDocumentFrequency) {
        Map<String, Double> tfidf = new HashMap<>();

        for (Map.Entry<String, Integer> entry : termFrequency.entrySet()) {
            String term = entry.getKey();
            int tf = entry.getValue();
            double idf = inverseDocumentFrequency.getOrDefault(term, 0.0);
            double tfidfValue = tf * idf;
            tfidf.put(term, tfidfValue);
        }

        return tfidf;
    }

    // sortowanie TFIDF oraz zwrócenie N wyrazów
    public String extractKeywords(Map<String, Double> tfidf, int n) {
        StringBuilder stringBuilder = new StringBuilder();
        List<Map.Entry<String, Double>> sortedList = tfidf
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toList());

        int i = 0;
        for (Map.Entry<String, Double> entry : sortedList) {
            if (i < n) {
                stringBuilder.append("Słowo: " + entry.getKey() + ", TF-IDF: " + entry.getValue() + "\n"); //dodanie wyrazów do SB
                i++;
            }
        }

        return stringBuilder.toString(); //zwrócenie wyrazów

    }

}
