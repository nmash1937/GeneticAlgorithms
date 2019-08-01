import java.io.File;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

/**
 * This class solves the knapsack problem, with a max weight of the backpack as BACKPACK_WEIGHT_TOTAL, genetic crossover
 * points of CROSSOVER, and a population size of POP_SIZE
 * 
 * known max of program is 832 fitness 
 * 
	private static int POP_SIZE = 100;
	private static int BACKPACK_WEIGHT_TOTAL = 500;
	private static int CROSSOVER = 10;
	private static int EPOCHS = 1000;
	private static int FITNESS_EXP = 3
	
	
 * @author Nick Masciandaro
 */
public class Knapsack {

	public static void main(String[] args) {
		try {
			Knapsack k = new Knapsack("knapsack500.dat", ITEMS_SIZE);
			k.train();
		} catch (FileNotFoundException e) {
			System.out.println("File knapsack.dat ain't there.");
		}
		
	}
	
	private class Item {
		int weight;
		int value;
	}
	
	//parameters
	private ArrayList<Item> items;
	private String pop[];
	private static int fitness[];
	
	//Constants CHANGE TO CUSTOMIZE GENETIC ALGORITHM
	final private static int POP_SIZE = 100;
	final private static int BACKPACK_WEIGHT_TOTAL = 500;
	final private static int CROSSOVER = 10;
	final private static int EPOCHS = 1000;
	final private static int FITNESS_EXP = 3;
	final private static int ITEMS_SIZE = 500;
	
	//constructor
	public Knapsack(String filename, int numItems) throws FileNotFoundException{
		items = new ArrayList<Item>(numItems);
		readData(filename, numItems);
		System.out.println(items.size());
		pop = genPop(POP_SIZE);
		fitness = new int[POP_SIZE];
	}
	
	public void train() {
		fitness = calcFit();
		int counter = 0;
		String ultimateS = "";
		int ultimateF = 0;
		for(int i=0; i<EPOCHS;i++) {
			String matingPool[] = roulette();
			Pair parents[] = new Pair[matingPool.length/2];
			for(int x=0; x<matingPool.length;x+=2) {
				parents[x/2] = new Pair(matingPool[x], matingPool[x+1]);
			}
			String children[] = procreate(parents, CROSSOVER);
			//System.out.println();
			pop = children;
			fitness = calcFit();
			
			int max = 0;
			int index = 0;
			for(int j=0; j<POP_SIZE;j++) {
				if(fitness[j]>max) {
					max=fitness[j];
					index = j;
				}
				if(fitness[j]>ultimateF) {
					ultimateS = pop[j];
					ultimateF = fitness[j];
				}
			}
			if(counter%1000==0) {
				System.out.println(index + " was max with fitness "+max + " with weight of " + calcW(pop[index]));
			
				calcW(pop[index]);
				//System.out.println(pop[index]);
			}
			counter++;
			if(counter%10==0)homogenous();
		}
		System.out.println("DONE");
		System.out.println("top answer: "+ultimateS);
		System.out.println("Top fitness: "+ ultimateF);
	}
	
	/**
	 * checks if the population 75% homogenious to the index of 1, and if so randomized half the population
	 */
	public void homogenous() {
		String same = pop[1];
		int counter = 0;
		for(String s : pop) {
			if(s.endsWith(same))counter++;
		}
		
		//System.out.println("homogenous mix is "+(double)counter/POP_SIZE);
		if((double)counter/POP_SIZE>.75) {
			//System.out.println("MIX");
			String n[] = genPop(POP_SIZE/2);
			for(int i=0; i<n.length;i++) {
				//System.out.println(n[i]);
				Random r = new Random();
				pop[r.nextInt(POP_SIZE)] = n[i];
			}
			//System.out.println();
		}
		

	}
	
	/**
	 * takes 2 parents and creates 2 children, of valid weights
	 * @param parents, pre paired up parents
	 * @param cross number of crossover points
	 * @return the children of the parents
	 */
	public String[] procreate(Pair parents[], int cross) {
		String children[] = new String[pop.length];
		cross++;
		int index = 0;
		for(Pair p : parents) {
			//System.out.println("Paren: "+p.a);
			//System.out.println("Paren: "+p.b);
			do {
				children[index] = crossover(cross, p.b, p.a);
				mutation(children, index);
				
			}while(!(calcW(children[index])<BACKPACK_WEIGHT_TOTAL));
			index++;
			
			do {
				children[index] = crossover(cross, p.a, p.b);
				mutation(children, index);
			}while(!(calcW(children[index])<BACKPACK_WEIGHT_TOTAL));
			index++;

			//System.out.println("child: "+children[index-1]);
			//System.out.println("child: "+children[index-2]+'\n');
		}
		
		return children;
	}
	
	/**
	 * private method for procreate that mixes up the strings
	 * NOTE: to create 2 children, swap p1 and p2 when calling
	 * @param cross amount of crossover points
	 * @param p1 starting parent
	 * @param p2 following parent
	 * @return
	 */
	private String crossover(int cross, String p1, String p2) {
		String res = "";
		Random r = new Random();
		
		int c[] = new int[cross+2];
		c[0] = 0;
		for(int i=1; i<cross;i++) {
			c[i] = r.nextInt(ITEMS_SIZE);
		}
		c[cross] = ITEMS_SIZE;
		Arrays.sort(c);
		for(int i=0; i<cross+1;i++) {
			String dad = i%2 == 0 ? p1 : p2;
			res+= dad.substring(c[i],c[i+1]);
		}
		return res;
	}
	
	/**
	 * calculates the weight of the string
	 * @param s the string of the pop
	 * @return integer value of weight
	 */
	public int calcW(String s) {
		int count = 0;
		int index = 0;
		for(char c : s.toCharArray()) {
			if(c=='1') {
				count += items.get(index).weight;
			}
			index++;
		}
		return count;
	}
	
	/**
	 * takes an array with index of potential mutee, and mutates the string of 100 bits with a 
	 * percentage of .1% per bit average (one in a thousand)
	 * @param children child array
	 * @param index index of child in array
	 */
	public void mutation(String children[], int index) {
		Random r = new Random();
		int mutation = r.nextInt(10);
		if(mutation==1) {
			int mut = r.nextInt(ITEMS_SIZE);
			String rep = "";
			if(children[index].charAt(mut)=='0') rep = "1"; else rep="0";
			children[index] = children[index].substring(0,mut)+rep+children[index].substring(mut+1);
		}
	}
	
	/**
	 * creates a mating pool by taking the fitness of the population, creating probabilities of mating, and 
	 * scaling them up to round numbers and then using the roulette algorithm, creating the mating pool
	 * NOTE: fitness is the value ^3, to make fitness exponentially important
	 * @return
	 */
	public String[] roulette() {
		int sum = 0;
		String matingPool[] = new String[pop.length];
		
		for(int i=0;i<fitness.length;i++) {
			sum+= fitness[i];
		}
		
		
		float prob[] = new float[fitness.length];
		float roul[] = new float[fitness.length];
		int sumRoul = 0;
		
		for(int i=0; i<prob.length;i++) {
			prob[i] = (float)fitness[i]/(float)sum;
			
			roul[i] = (float) Math.pow(Math.round((float)prob[i]*1000),FITNESS_EXP);
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
	
	/**
	 * generates a population of a given integer
	 * @param num how many individuals 
	 * @return num number of random inputs
	 */
	public String[] genPop(int num) {
		String p[] = new String[num];
		
		for(int x=0; x<num;x++) {
			Random r = new Random();	
			int accum = 0;
			String res = "";
			ArrayList<Integer> nums = new ArrayList<Integer>();
			
			DONE: for(int i=0; i<ITEMS_SIZE;i++) {
				int selection = r.nextInt(ITEMS_SIZE);
				Item item = items.get(selection);
				if(accum+item.weight>BACKPACK_WEIGHT_TOTAL) {
					break DONE;
				}else {
					nums.add(selection);
					accum+=item.weight;
				}
			}
			
			for(int i=0;i<ITEMS_SIZE;i++) {
				if(nums.contains(i)) {
					res+="1";
				}else {
					res+="0";
				}
			}
			p[x] = res;

		}
		return p;
	}
	
	/**
	 * calculates the fitness of the current population and returns the fitness
	 * @return the fitness array of the current population
	 */
	public int[] calcFit() {
		int fit[] = new int[POP_SIZE];
		int index = 0;
		for(String s : pop) {
			int accum = 0;
			int count = 0;
			for(char c : s.toCharArray()) {
				if(c == '1') {
					accum += items.get(count).value;
					count++;
				}
			}
			fit[index] = accum;
			index++;
		}
		return fit;
	}
	
	public void readData(String filename, int numItems) throws FileNotFoundException{
		Scanner in = new Scanner(new File(filename));
		for(int i=0; i<numItems; i++) {
			String[] line = in.nextLine().split(" ");
			Item item = new Item();
			item.weight = Integer.parseInt(line[0]);
			item.value = Integer.parseInt(line[1]);
			items.add(item);
		}
		in.close();
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
