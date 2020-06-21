import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class CSVDataLoader {

    public static void main(String[] args) throws FileNotFoundException {
        List<Iris> irisList = loadIrisData("src/iris.csv");
        List<Iris> trainList = makeTrainSet(irisList);
        List<Iris> vectorList = loadIrisData("src/iris.csv");
        List<Iris> testList = makeTestSet(vectorList);

//        saveSet("src/train-set.csv", trainList);
//        saveSet("src/test-set.csv", testList);

        Scanner kScanner = new Scanner(System.in);
        System.out.println("Podaj K");
        String k = kScanner.nextLine();

        List<Iris> predictedIrises = KNN(Integer.parseInt(k), trainList, testList);

        for(Iris predicedIris : predictedIrises) {
            System.out.println(predicedIris);
        }
        System.out.println("Podane K to: " + k);
        getAccuracy(testList, predictedIrises);

        List<Iris> typedVectors = new ArrayList<>();
        Scanner vectorScanner = new Scanner(System.in);
        boolean loadingVector = true;

        while(loadingVector) {
            System.out.println("Podaj ilosc wektorow:");
            int vectors = vectorScanner.nextInt();

            Iris[][] vecArgs = new Iris[vectors][4];

            for(int i = 0; i < vectors; i++) {
                System.out.println("Nowy wektor:");
                for(int j = 0; j < 1; j++) {
                    System.out.println("Podaj argumenty:");
                    vecArgs[i][j] = new Iris(vectorScanner.nextDouble(), vectorScanner.nextDouble(),
                            vectorScanner.nextDouble(), vectorScanner.nextDouble(), "");
                    typedVectors.add(vecArgs[i][j]);
                }
            }
            loadingVector = false;
        }

        Scanner k2Scanner = new Scanner(System.in);
        System.out.println("Podaj K:");
        String k2 = k2Scanner.nextLine();

        List<Iris> newPredictions = KNN(Integer.parseInt(k2), trainList, typedVectors);
        for(Iris newpredicts : newPredictions) {
            System.out.println(newpredicts);
        }
    }

    //zapisywanie Danych Iris.csv do Listy "irisList", "vectorList"
    public static List<Iris> loadIrisData(String fileName) {
        List<Iris> irisList = new ArrayList<>();
        Path filePath = Paths.get(fileName);

        try(BufferedReader br = Files.newBufferedReader(filePath, StandardCharsets.US_ASCII)) {

            String line = br.readLine();//czytanie wierszami
            while(line != null) {
                String[] args = line.split(";");
                double[] vector = {Double.parseDouble(args[0]), Double.parseDouble(args[1]),
                                Double.parseDouble(args[2]), Double.parseDouble(args[3])};//dane Irysow
                String variant = args[4];

                Iris iris = createIris(vector, variant);
                irisList.add(iris);

                line = br.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return irisList;
    }

    public static Iris createIris(double[] data, String variant) throws FileNotFoundException {
        double sepalLength = data[0];
        double sepalWidth = data[1];
        double petalLength = data[2];
        double petalWidth = data[3];

        return new Iris(sepalLength, sepalWidth, petalLength, petalWidth, variant);
    }

    //zapisywanie wybranych irisow do "trainList"
    public static List<Iris> makeTrainSet(List<Iris> irisList) {
        List<Iris> trainSet = new ArrayList<>();
        int count = 0;
        ListIterator<Iris> irisListIterator = irisList.listIterator();
        while(irisListIterator.hasNext() && count < 150) {
                if (count <= 34 || count >= 50 && count <= 84 || count >= 100 && count <= 134) {
                    trainSet.add(irisList.get(count));
                }
            count++;
        }
        return trainSet;
    }

    //zapisywanie wybranych irisow do "testList"
    public static List<Iris> makeTestSet(List<Iris> vectorsList) {
        List<Iris> testSet = new ArrayList<>();
        int count = 0;
        ListIterator<Iris> vectorsListIterator = vectorsList.listIterator();
        while(vectorsListIterator.hasNext() && count < 150) {
            if(count>=35 && count <= 49 || count>=85 && count<=99 || count>=135) {
                testSet.add(vectorsList.get(count));
            }
            count++;
        }
        return testSet;
    }

    //metoda tworzaca plik "train-set.csv", "test-set.csv"
    public static void saveSet(String filePath, List<Iris> iList) {
        File csvFile = new File(filePath);
        try {
            FileWriter writer = new FileWriter(csvFile);
            for(Iris i : iList) {
                List<String> list = new ArrayList<>();
                list.add(String.valueOf(i.getSepalLength()));
                list.add(String.valueOf(i.getSepalWidth()));
                list.add(String.valueOf(i.getPetalLength()));
                list.add(String.valueOf(i.getSepalWidth()));
                list.add(i.getIrisVariant());

                CSVUtils.writeLine(writer, list);
            }

            writer.flush();
            writer.close();

            } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Iris> KNN(int k, List<Iris> trainList, List<Iris> testList) throws FileNotFoundException {
        Map<Iris, Double> distances = new HashMap<>();
        List<Iris> sorted = new ArrayList<>();

        for(int i = 0; i < testList.size(); i++) {
            for(int j = 0; j < trainList.size(); j++) {
                double sepalLPow = Math.pow(testList.get(i).getSepalLength() - trainList.get(j).getSepalLength(), 2);
                double sepalWPow = Math.pow(testList.get(i).getSepalWidth() - trainList.get(j).getSepalWidth(), 2);
                double petalLPow = Math.pow(testList.get(i).getPetalLength() - trainList.get(j).getPetalLength(), 2);
                double petalWPow = Math.pow(testList.get(i).getPetalWidth() - trainList.get(j).getPetalWidth(), 2);

                distances.put(trainList.get(j), Math.sqrt(sepalLPow + sepalWPow + petalLPow + petalWPow));
            }
            Map<Iris, Double> sortedMap = sortByDistance(distances);

            Map<Iris, Double> kSmallestDistances = new LinkedHashMap<>();
            int count = 0;

            for(Map.Entry<Iris, Double> entry: sortedMap.entrySet()) {
                if(count < k) {
                    kSmallestDistances.put(entry.getKey(), entry.getValue());
                    count++;
               }
            }

            List<Iris> kclosestIrises = new ArrayList<>(kSmallestDistances.keySet());
            int setosaCounter = 0;
            int versicolorCounter = 0;
            int virginicaCounter = 0;
            for(Iris iris : kclosestIrises) {
                if(iris.getIrisVariant().equals("Iris-setosa")) {
                    setosaCounter++;
                }
                if(iris.getIrisVariant().equals("Iris-versicolor")) {
                    versicolorCounter++;
                }
                if(iris.getIrisVariant().equals("Iris-virginica")) {
                    virginicaCounter++;
                }
            }

            if(setosaCounter > versicolorCounter && setosaCounter >= virginicaCounter) {
                sorted.add(new Iris(testList.get(i).getSepalLength(), testList.get(i).getSepalWidth(), testList.get(i).getPetalLength(), testList.get(i).getPetalWidth(), "Iris-setosa"));
            }
            if(versicolorCounter >= setosaCounter && versicolorCounter > virginicaCounter) {
                sorted.add(new Iris(testList.get(i).getSepalLength(), testList.get(i).getSepalWidth(), testList.get(i).getPetalLength(), testList.get(i).getPetalWidth(), "Iris-versicolor"));
            }
            if(virginicaCounter > setosaCounter && virginicaCounter >= versicolorCounter) {
                sorted.add(new Iris(testList.get(i).getSepalLength(), testList.get(i).getSepalWidth(), testList.get(i).getPetalLength(), testList.get(i).getPetalWidth(), "Iris-virginica"));
            }
        }
        return sorted;
    }

    //sortowanie mapy pod wzgledem dystansu
    public static Map<Iris, Double> sortByDistance(Map<Iris, Double> hmap) {
        List<Map.Entry<Iris, Double> > list = new LinkedList<>(hmap.entrySet());

        list.sort(Map.Entry.comparingByValue());

        Map<Iris, Double> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<Iris, Double> aa : list) {
            sortedMap.put(aa.getKey(), aa.getValue());
        }
        return sortedMap;
    }

    public static void getAccuracy(List<Iris> testList, List<Iris> predictedList) {
        int countSameVariant = 0;

        for(int i = 0; i < testList.size(); i++) {
            if(testList.get(i).getIrisVariant().equals(predictedList.get(i).getIrisVariant()))
                countSameVariant++;
        }
        System.out.println(countSameVariant + "/" + testList.size());
        float accuracy =  100 * (float)(countSameVariant/testList.size());
        System.out.println("Accuracy: " + accuracy + "%");
    }
}