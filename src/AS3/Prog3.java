package AS3;

/*=============================================================================
|   Assignment:  Program #3:  Columnsort
|       Author:  Jack Williams (jackmwilliams@arizona.edu)
|       Grader:  Unsure
|
|       Course:  CSC 345
|   Instructor:  L. McCann
|     Due Date:  3:30 p.m. October 31, 2024
|
|  Description:  Provides an implementation of the Columnsort algorithm,
|                    an algorithm that permutes a list of data into a matrix
|                    and sorts its data (in Column Major Order) by sorting each
|                    column and permuting them in certain ways (described below).
|                    Reads integer data from a command line argument, performs
|                    the column sort (timing execution time), and prints
|                    matrix length/num_rows (r)/num_cols (s), time taken,
|                    and the sorted data, one integer per line.
|                    There are 8 steps to the algorithm:
|                    Steps 1, 3, 5, 7: Sort each column individually.
|                    Step 2: Transpose and reshape, where the values in each
|                        column get transposed to some number of rows.
|                    Step 4: Undo Step 2
|                    Step 6: Shift elements down by [COLSIZE]/2 elements,
|                        creating one additional column and filling the first
|                        half of col #1 and the second half of col #2
|                        with positive and negative infinity, respectively.
|                    Step 8: Undo Step 6
|
|     Language:  Java 17
| Ex. Packages:  None.
|                
| Deficiencies:  A rather inefficient sorting algorithm (selection sort) has
|                    been used on the odd-numbered steps. Some edge cases have
|                    not been tested. Modularization is subpar.
*===========================================================================*/

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Prog3 {

	public static void main(String[] args) {
		final double BILLION = 1000000000.0;	// for calculating nanoseconds taken
		
		// Read the Integers from the file:
		Integer[] nums = readIntsFromFile(args[0]);
		
		// Start columnsort timer:
		long startTime = System.nanoTime();
		
		// Perform columnsort:
		Integer[][] sorted2D = columnSort(nums);
		
		// Stop timer:
		long endTime = System.nanoTime();
		long timeTaken = endTime - startTime;
		double secondsTaken = timeTaken / BILLION;
		
		// Output results:
		int r = sorted2D.length;	// num rows
		int s = (r>0 ? sorted2D[0].length : 0);	// num cols
		int n = r * s;				// num total elements
		
		System.out.println("n = " + n);
		System.out.println("r = " + r);
		System.out.println("s = " + s);
		System.out.println(String.format("Elapsed time = %.3f seconds.", secondsTaken));
		for (int i = 0; i < n; i++) {
			System.out.println(readCMO(sorted2D, i));
		}
	}
	
	/*---------------------------------------------------------------------
	|  Method readIntsFromFile(String fName)
	|
	|  Purpose:  Reads integers from a file, returning an array containing these.
	|                Exits program if file cannot be read or contains any
	|                non-integer values.
	|
	|  Pre-condition:  None (validity of filename tested within).
	|
	|  Post-condition: The integers will be read, collected, and returned.
	|
	|  Parameters:
	|      String fName -- the name of the file to read from
	|                          (likely given at the command line).
	|
	|  Returns:  An Integer[] with each read element.
	*-------------------------------------------------------------------*/
	private static Integer[] readIntsFromFile(String fName) {
		// Read the input file into an ArrayList (turning into normal array once read):
		ArrayList<Integer> numsArrayList = new ArrayList<>();
		try {
			Scanner inputScan = new Scanner(new File(fName));
			
			while (inputScan.hasNextInt()) {
				numsArrayList.add(Integer.valueOf(inputScan.nextLine()));
			}
			
			inputScan.close();
		} catch (FileNotFoundException e) {	// if file not found
			System.out.println("Error: Cannot read file.");
			e.printStackTrace();
			System.exit(1);
		} catch (NumberFormatException e) {	// if any non-integer values
			System.out.println("Error: All input values must be integers.");
			e.printStackTrace();
			System.exit(1);
		}
		
		// Convert input ArrayList into a normal array for more accurate timing:
		return (Integer[]) numsArrayList.toArray(new Integer[numsArrayList.size()]);
	}
	
	/*---------------------------------------------------------------------
	|  Method columnSort(Integer[] nums)
	|
	|  Purpose:  Performs a columnsort on the accepted Integer[] nums, returning
	|                the generated 2D array, where elements are sorted when read
	|                using Column Major Order.
	|                (Note: May terminate program, if factor pairs cannot be generated.)
	|
	|  Pre-condition:  nums has been fully populated.
	|
	|  Post-condition: The array will be sorted with columnsort, with the resulting
	|                      matrix returned.
	|
	|  Parameters:
	|      Integer[] nums -- the array of Integers to sort.
	|
	|  Returns:  The sorted (when read CMO) 2D array.
	*-------------------------------------------------------------------*/
	private static Integer[][] columnSort(Integer[] nums) {
		int len = nums.length;
		
		// If empty, just return empty 2D array:
		if (len == 0) {
			return new Integer[0][0];
		}
		
		// Get the factors of nums.length, preserving pairs:
		// (Note: The smaller integer in the pair will come first.)
		ArrayList<Integer[]> factors = new ArrayList<>();
		for (int f = (int) Math.sqrt(len); f > 0; f--) {
			// If a factor pair, add it:
			if (len % f == 0) {
				factors.add(new Integer[] {f, len / f});
			}
		}
		
		// Find the number of rows (r) and cols (s) to use:
		int r = 0, s = 0;
		for (Integer[] pair : factors) {
			int smaller = pair[0];
			int larger = pair[1];
			
			// If factor pair is valid, set to r and s:
			if (larger % smaller == 0 && larger >= 2*Math.pow(smaller-1, 2)) {
				// Successful pair found, so set to r and s:
				r = larger;
				s = smaller;
				break;
			}
		}
		
		// If r or s is still 0, something went wrong:
		if (r == 0 || s == 0) {
			System.out.println("Error: Number of rows or columns could not be generated.");
			System.exit(1);
		}
		
		// Create a 2D array (with r rows and s cols) containing each element:
		Integer[][] grid = new Integer[r][s];
		for (int i = 0; i < nums.length; i++) {
			writeCMO(grid, i, nums[i]);
		}
		
		// If only one column, just use a simpler sort and return:
		if (s == 1) {
			columnSelectionSort(grid, 0);
			return grid;
		}
		
		// Perform the 8 steps of the columnsort algorithm:
		
		// Step 1: Sort each column:
		for (int colI = 0; colI < s; colI++) {
			columnSelectionSort(grid, colI);
		}
		
		// Step 2: Transpose and reshape:
		grid = transposeReshape(grid);
		
		// Step 3: Sort each column:
		for (int colI = 0; colI < s; colI++) {
			columnSelectionSort(grid, colI);
		}
		
		// Step 4: Reshape and transpose (undo Step 2):
		grid = reshapeTranspose(grid);
		
		// Step 5: Sort each column:
		for (int colI = 0; colI < s; colI++) {
			columnSelectionSort(grid, colI);
		}
		
		// Step 6: Shift down by r/2:
		Integer[][] shiftedDown = shiftDown(grid);
		
		// Step 7: Sort each column:
		for (int colI = 0; colI < s; colI++) {
			columnSelectionSort(shiftedDown, colI);
		}
		
		// Step 8: Shift up by r/2 (undo Step 6):
		grid = shiftUp(shiftedDown);
		
		return grid;
	}
	
	/*---------------------------------------------------------------------
	|  Method columnSelectionSort(Integer[][] grid, int col)
	|
	|  Purpose:  Sorts a single column of a 2D Integer array in ascending
	|                order (with grid[0][col] being the lowest val),
	|                using the selection sort algorithm.
	|
	|  Pre-condition:  col is a valid column index (0 <= col < grid[0].length).
	|
	|  Post-condition: The "col"th column will be sorted.
	|
	|  Parameters:
	|      Integer[][] grid -- the 2D array of Integers to sort a column of.
	|      int col -- the column of the grid to sort.
	|
	|  Returns:  None.
	*-------------------------------------------------------------------*/
	private static void columnSelectionSort(Integer[][] grid, int col) {
		for (int row = 0; row < grid.length-1; row++) {
			// Find minimum non-included value:
			int minI = row;
			for (int compI = minI+1; compI < grid.length; compI++) {
				if (grid[compI][col] < grid[minI][col]) {
					minI = compI;
				}
			}
			
			// Swap minimum into its spot in the list:
			Integer temp = grid[row][col];
			grid[row][col] = grid[minI][col];
			grid[minI][col] = temp;
		}
	}
	
	/*---------------------------------------------------------------------
	|  Method transposeReshape(Integer[][] grid)
	|
	|  Purpose:  Reorders the items in the accepted 2D array such that
	|                the elements in each column c are transfered to r rows
	|                (where r = [NUMROWS] / [NUMCOLS]), kept in order going down.
	|                (In other words, it reads the grid with Column Major Order,
	|                and inserts with Row Major Order.)
	|                Returns the reordered 2D array.
	|
	|  Pre-condition:  [NUMCOLS] is a divisor of [NUMROWS].
	|
	|  Post-condition: The 2D transposed array will be generated and returned.
	|
	|  Parameters:
	|      Integer[][] grid -- the 2D array to transpose.
	|
	|  Returns:  The transposed 2D array.
	*-------------------------------------------------------------------*/
	private static Integer[][] transposeReshape(Integer[][] grid) {
		int rows = grid.length;
		int cols = grid[0].length;
		int len = rows * cols;
		
		Integer[][] transposed = new Integer[rows][cols];
		
		// Read grid by column, and insert into transposed by row:
		for (int i = 0; i < len; i++) {
			writeRMO(transposed, i, readCMO(grid, i));
		}
		
		return transposed;
	}
	
	/*---------------------------------------------------------------------
	|  Method reshapeTranspose(Integer[][] grid)
	|
	|  Purpose:  Reorders the items in the accepted 2D array such that
	|                each column c accepts the elements in r rows
	|                (where r = [NUMROWS] / [NUMCOLS]), kept in order going down.
	|                (In other words, it reads the grid with Row Major Order,
	|                and inserts with Column Major Order.)
	|                Returns the reordered 2D array.
	|
	|  Pre-condition:  [NUMCOLS] is a divisor of [NUMROWS].
	|
	|  Post-condition: The 2D transposed array will be generated and returned.
	|
	|  Parameters:
	|      Integer[][] grid -- the 2D array to transpose.
	|
	|  Returns:  The transposed 2D array.
	*-------------------------------------------------------------------*/
	private static Integer[][] reshapeTranspose(Integer[][] grid) {
		int rows = grid.length;
		int cols = grid[0].length;
		int len = rows * cols;
		
		Integer[][] transposed = new Integer[grid.length][grid[0].length];
		
		// Read grid by row, and insert into transposed by column:
		for (int i = 0; i < len; i++) {
			writeCMO(transposed, i, readRMO(grid, i));
		}
		
		return transposed;
	}
	
	/*---------------------------------------------------------------------
	|  Method shiftDown(Integer[][] grid)
	|
	|  Purpose:  Shifts each element in the accepted grid down by [NUMROWS]/2
	|                values, moving to the top of the next column if necessary.
	|                The resulting array will have one more column than the
	|                starting grid, and the first half of the first column
	|                and the second half of the last column will be filed with
	|                Integer.MIN_VALUE and Integer.MAX_VALUE, respectively.
	|                Returns the shifted 2D array.
	|
	|  Pre-condition:  None.
	|
	|  Post-condition: The shifted array will be generated and returned.
	|
	|  Parameters:
	|      Integer[][] grid -- the 2D array to shift.
	|
	|  Returns:  The shifted 2D array (with one more row than grid has).
	*-------------------------------------------------------------------*/
	private static Integer[][] shiftDown(Integer[][] grid) {
		int rows = grid.length;
		int cols = grid[0].length;
		int len = rows * cols;
		
		Integer[][] shifted = new Integer[rows][cols+1];
		
		int shiftBy = rows / 2;
		int i = 0;
		
		// Add the first (shiftBy) minValue values to the first column:
		for (/*init above*/; i < shiftBy; i++) {
			writeCMO(shifted, i, Integer.MIN_VALUE);
		}
		
		// Insert shifted values from grid:
		for (/*init above*/; i < len + shiftBy; i++) {
			writeCMO(shifted, i, readCMO(grid, i - shiftBy));
		}
		
		// Add the last (shiftBy) maxValue values to the last column:
		for (/*init above*/; i < len + rows; i++) {
			writeCMO(shifted, i, Integer.MAX_VALUE);
		}
		
		return shifted;
	}
	
	/*---------------------------------------------------------------------
	|  Method shiftUp(Integer[][] grid)
	|
	|  Purpose:  Reverses the effect of the shiftDown() method. More specifically,
	|                shifts each element in the accepted grid up by [NUMROWS]/2
	|                values, moving to the top of the previous column if necessary.
	|                The resulting array will have one fewer column than the
	|                starting grid, and the elements in the first half of the
	|                first column and the second half of the last column will be
	|                removed from the array (we assume they are Integer.[MIN/MAX]_VALUE).
	|                Returns the shifted 2D array.
	|
	|  Pre-condition:  The accepted grid has been shifted down by [NUMROWS]/2 values.
	|
	|  Post-condition: The shifted array will be generated and returned.
	|
	|  Parameters:
	|      Integer[][] grid -- the 2D array to shift.
	|
	|  Returns:  The shifted 2D array (with one fewer row than grid has).
	*-------------------------------------------------------------------*/
	private static Integer[][] shiftUp(Integer[][] grid) {
		int rows = grid.length;
		int cols = grid[0].length;
		int len = rows * cols;
		
		Integer[][] shifted = new Integer[rows][cols-1];
		
		int shiftBy = rows / 2;
		
		// Insert shifted values from grid. The first and last (shiftBy)
		// elements will be skipped, assuming [MIN/MAX]_VALUE:
		for (int i = 0; i < len - rows; i++) {
			writeCMO(shifted, i, readCMO(grid, i + shiftBy));
		}
		
		return shifted;
	}
	
	/*---------------------------------------------------------------------
	|  Method readRMO(Integer[][] grid, int i)
	|
	|  Purpose:  Returns the "i"th element in the grid,
	|                read in Row Major Order.
	|
	|  Pre-condition:  0 <= i < [number of grid elements].
	|
	|  Post-condition: The correct value will be accessed and returned.
	|
	|  Parameters:
	|      Integer[][] grid -- The grid to read from.
	|      int i -- the index to read at, using Row Major Order.
	|
	|  Returns:  The accessed element.
	*-------------------------------------------------------------------*/
	private static Integer readRMO(Integer[][] grid, int i) {
		int cols = grid[0].length;
		
		int rowI = i / cols;
		int colI = i % cols;
		
		return grid[rowI][colI];
	}
	
	/*---------------------------------------------------------------------
	|  Method writeRMO(Integer[][] grid, int i, Integer val)
	|
	|  Purpose:  Sets the "i"th element in the grid to the accepted val,
	|                written in Row Major Order.
	|
	|  Pre-condition:  0 <= i < [number of grid elements].
	|
	|  Post-condition: The correct value will be written.
	|
	|  Parameters:
	|      Integer[][] grid -- The grid to write to.
	|      int i -- the index to write at, using Row Major Order.
	|      Integer val -- the value to write.
	|
	|  Returns:  None.
	*-------------------------------------------------------------------*/
	private static void writeRMO(Integer[][] grid, int i, Integer val) {
		int cols = grid[0].length;
		
		int rowI = i / cols;
		int colI = i % cols;
		
		grid[rowI][colI] = val;
	}
	
	/*---------------------------------------------------------------------
	|  Method readCMO(Integer[][] grid, int i)
	|
	|  Purpose:  Returns the "i"th element in the grid,
	|                read in Column Major Order.
	|
	|  Pre-condition:  0 <= i < [number of grid elements].
	|
	|  Post-condition: The correct value will be accessed and returned.
	|
	|  Parameters:
	|      Integer[][] grid -- The grid to read from.
	|      int i -- the index to read at, using Column Major Order.
	|
	|  Returns:  The accessed element.
	*-------------------------------------------------------------------*/
	private static Integer readCMO(Integer[][] grid, int i) {
		int rows = grid.length;
		
		int rowI = i % rows;
		int colI = i / rows;
		
		return grid[rowI][colI];
	}
	
	/*---------------------------------------------------------------------
	|  Method writeCMO(Integer[][] grid, int i, Integer val)
	|
	|  Purpose:  Sets the "i"th element in the grid to the accepted val,
	|                written in Column Major Order.
	|
	|  Pre-condition:  0 <= i < [number of grid elements].
	|
	|  Post-condition: The correct value will be written.
	|
	|  Parameters:
	|      Integer[][] grid -- The grid to write to.
	|      int i -- the index to write at, using Column Major Order.
	|      Integer val -- the value to write.
	|
	|  Returns:  None.
	*-------------------------------------------------------------------*/
	private static void writeCMO(Integer[][] grid, int i, Integer val) {
		int rows = grid.length;
		
		int rowI = i % rows;
		int colI = i / rows;
		
		grid[rowI][colI] = val;
	}
}
