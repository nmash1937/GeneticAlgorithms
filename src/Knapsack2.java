import java.util.Random;

public class Knapsack2 {
	//Constants
	public static int POP_SIZE = 100; //Must be even number to pair parents
	public static int FITNESS_ONE_IN =1000;
	public static int counter;
	public static int KNAP_SIZE = 500;
	
	//Parameters
	String pop[];
	double fitness[];
	
	public static void main(String[] args) {
		Knapsack2 g = new Knapsack2();
		g.train();
	}
	
	/**
	 * Constructor
	 */
	public Knapsack2() {
		pop = new String[POP_SIZE];
		fitness = new double[POP_SIZE];
		counter = 0;
	}
	
	public void calcFit() {
		for(int i=0; i<pop.length;i++) {
			fitness[i] = fitness(pop[i]);
		}
	}
	
	public void train() {
		genRandPop();
		calcFit();
		while(!solutionFound()){
			
			String matingPool[] = routlette();
			
			Pair parents[] = new Pair[matingPool.length/2];
			for(int x=0; x<matingPool.length;x+=2) {
				parents[x/2] = new Pair(matingPool[x], matingPool[x+1]);
			}
			String children[] = procreate(parents);
			
			System.out.println();
			pop = children;
			counter++;
			calcFit();
			System.out.println("Children: ");
			for(int x=0; x<pop.length;x++) {
				System.out.println(pop[x] + "\t" + fitness[x]);
			}
			System.out.println();
			calcFit();
		}
	}
	
	public boolean solutionFound() {
		for(int i=0; i<pop.length;i++) {
			if(fitness[i]>FITNESS_ONE_IN-1) {
				System.out.println("Solution found: "+pop[i] +" or "+toInt(pop[i]));
				System.out.println("Count: "+counter);
				return true;
			}
		}
		return false;
	}
	
	public String[] procreate(Pair parents[]) {
		Random r = new Random();
		int crossover = 0;
		String children[] = new String[pop.length];
		int index = 0;
		for(Pair p : parents) {
			crossover = r.nextInt(7);
			children[index] = p.a.substring(0,crossover)+p.b.substring(crossover);
			mutation(children, index);
			index++;
			children[index] = p.b.substring(0,crossover)+p.a.substring(crossover);
			mutation(children, index);
			index++;
		}
		return children;
	}
	
	public void mutation(String children[], int index) {
		Random r = new Random();
		int mutation = r.nextInt(100);
		if(mutation==1) {
			System.out.println("MUTATION!!!!!");
			int mut = r.nextInt(8);
			System.out.println("mutation at index "+mut);
			System.out.println("Before: "+children[index]);
			String rep = "";
			if(children[index].charAt(mut)=='0') rep = "1"; else rep="0";
			children[index] = children[index].substring(0,mut)+rep+children[index].substring(mut+1);
			System.out.println("After: "+children[index]);
		}
	}
	
	public String[] routlette() {
		int sum = 0;
		String matingPool[] = new String[pop.length];
		
		for(int i=0;i<fitness.length;i++) {
			sum+= fitness[i];
		}
		
		double prob[] = new double[fitness.length];
		double roul[] = new double[fitness.length];
		int sumRoul = 0;
		
		for(int i=0; i<prob.length;i++) {
			prob[i] = fitness[i]/sum;
			roul[i] =(int) Math.round(prob[i]*10);
			sumRoul += roul[i];
		}
		
		for(int i=0; i<pop.length;i++) {
			Random r = new Random();
			int selection = r.nextInt(sumRoul);
			
			int index = 0;
			int totals = 0;
			while(totals<=selection) {
				totals += roul[index];
				index++;
			}
			index--;
			matingPool[i] = pop[index];
		}
		
		return matingPool;
	}
	
	public void genRandPop() {
		//TODO
		
		
	}
	
	public double fitness(String a) {
		return 0;
		//TODO
	}
	
	public int toInt(String a) {
		int res = 0, count = a.length()-1;
		for(char c : a.toCharArray()) {
			if(c == '1') res += Math.pow(2, count);
			count --;
		}
		return res;
	}
	
	/**
	 * Class used for parents to pair
	 * @author Nick_Masciandaro
	 *
	 */
	public class Pair {
		String a;
		String b;
		public Pair(String a, String b) {
			this.a = a;
			this.b = b;
		}
	}
}
