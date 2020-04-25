import java.util.*;

public class Generate{
    private Random random;

    Generate(){
        random = new Random();
    }

    private int[] createNumbers(int n){
        // Generate base mathdoku, conforming with row and col constrictions
        int[] numbers = new int[n*n];

        for(int y=0; y<n; y++){
            for(int x=0; x<n; x++){
                numbers[x+y*n] = ((x+y) % n)+1;
            }
        }

        // Then swap columns and rows
        Random random = new Random();
        int a;
        int b;
        int[] temp = new int[n];
        for(int k=0; k<n-1; k++) {
            a = random.nextInt(n);
            b = random.nextInt(n);

            for (int j = 0; j < n; j++) {
                temp[j] = numbers[a+j*n];
            }
            for (int j = 0; j < n; j++) {
                numbers[a+j*n] = numbers[b+j*n];
                numbers[b+j*n] = temp[j];
            }

            a = random.nextInt(n);
            b = random.nextInt(n);

            for (int j = 0; j < n; j++) {
                temp[j] = numbers[j+a*n];
            }
            for (int j = 0; j < n; j++) {
                numbers[j+a*n] = numbers[j+b*n];
                numbers[j+b*n] = temp[j];
            }
        }
        return numbers;
    }

    private Cage makeCage(int n, int[] numbers, int[] cellIndexes){
        Cage newCage;
        if(cellIndexes.length == 1){
            newCage = new Cage(Operator.NONE, numbers[cellIndexes[0]], cellIndexes, n);
        }
        else{
            int total;

            float divTotal = 1;
            int subTotal = 0;
            int max = 0;
            for(int i: cellIndexes){
                subTotal += numbers[i];
                divTotal *= numbers[i];
                if(numbers[i] > max){
                    max = numbers[i];
                }
            }
            subTotal = 2*max - subTotal;
            divTotal = max*max/divTotal;

            int x  = random.nextInt(12);
            List<Operator> choices = new ArrayList<Operator>();
            choices.add(Operator.ADD);
            choices.add(Operator.MULTIPLY);
            choices.add(Operator.SUBTRACT);
            choices.add(Operator.DIVIDE);

            if(subTotal < 0)
                choices.remove(Operator.SUBTRACT);
            if(divTotal % 1 != 0)
                choices.remove(Operator.DIVIDE);

            Operator choice = choices.get(random.nextInt(choices.size()));

            if(choice == Operator.ADD){
                total = 0;
                for(int i: cellIndexes)
                    total += numbers[i];
                newCage = new Cage(Operator.ADD, total, cellIndexes, n);
            }else if(choice == Operator.MULTIPLY){
                total = 1;
                for(int i: cellIndexes)
                    total *= numbers[i];
                newCage = new Cage(Operator.MULTIPLY, total, cellIndexes, n);
            }else if(choice == Operator.SUBTRACT)
                newCage = new Cage(Operator.SUBTRACT, subTotal, cellIndexes, n);
            else
                newCage = new Cage(Operator.DIVIDE, (int) divTotal, cellIndexes, n);
        }
        return newCage;
    }

    private Cage[] makeCages(int n, int[] numbers){
        // Generate cage shapes
        HashSet<Integer> available = new HashSet<Integer>();
        for(int i=0; i<n*n; i++){
            available.add(i);
        }
        ArrayList<Cage> cages = new ArrayList<Cage>();
        ArrayList<Integer> cells;
        ArrayList<Integer> seeds;
        int first = -1;
        double p=0.6;
        int size;
        while(!available.isEmpty()){
            cells = new ArrayList<Integer>();
            seeds = new ArrayList<Integer>();

            for(first+=1; first<n*n; first++){
                if(available.contains(first)){
                    seeds.add(first);
                    available.remove(first);
                    break;
                }
            }
            size = 1;
            while(!seeds.isEmpty()){
                int s = seeds.get(0);
                if(s%n > 0 && available.contains(s-1) && random.nextDouble() < p/size){
                    seeds.add(s-1);
                    available.remove(s-1);
                    size++;
                }if(s%n < n-1 && available.contains(s+1) && random.nextDouble() < p/size){
                    seeds.add(s+1);
                    available.remove(s+1);
                    size++;
                }
                if(s/n < n-1 && available.contains(s+n) && random.nextDouble() < p/size){
                    seeds.add(s+n);
                    available.remove(s+n);
                    size++;
                }
                if(s/n > 0 && available.contains(s-n) && random.nextDouble() < p/size){
                    seeds.add(s-n);
                    available.remove(s-n);
                    size++;
                }
                seeds.remove(0);
                cells.add(s);
            }

            int[] cellIndexes = new int[cells.size()];
            for(int i=0; i<cells.size(); i++){
                cellIndexes[i] = cells.get(i);
            }
            Arrays.sort(cellIndexes);

            // Decide how to make cage
            Cage newCage = makeCage(n, numbers, cellIndexes);
            cages.add(newCage);
        }

        Cage[] newCages = new Cage[cages.size()];
        for(int i=0; i<cages.size(); i++){
            newCages[i] = cages.get(i);
        }
        return newCages;
    }

    Cage[] generate(int n){
        int[] numbers = createNumbers(n);
        return makeCages(n, numbers);
    }
}
