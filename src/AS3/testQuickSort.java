package AS3;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class testQuickSort {

	public static void main(String[] args) {
		testSortThree();
		testSortTwo();
		testSortFive();
		testSort03a();
	}
	
	public static void testSortThree() {
		Integer[][] ar = new Integer[3][2];
		ar[0][0] = 30;
		ar[1][0] = 20;
		ar[2][0] = 10;
		columnQuickSort(ar, 0);
		System.out.println(isSorted(ar));
	}
	
	public static void testSortTwo() {
		Integer[][] ar = new Integer[2][2];
		ar[0][0] = 30;
		ar[1][0] = 20;
		columnQuickSort(ar, 0);
		System.out.println(isSorted(ar));
	}
	
	public static void testSortFive() {
		Integer[][] ar = new Integer[5][2];
		ar[0][0] = 50;
		ar[1][0] = 40;
		ar[2][0] = 30;
		ar[3][0] = 20;
		ar[4][0] = 10;
		columnQuickSort(ar, 0);
		System.out.println(isSorted(ar));
	}
	
	public static void testSort03a() {
		Integer[][] ar = new Integer[100][2];
		try {
			Scanner read = new Scanner(new File("src/AS3/prog03a.dat"));
			int colI = 0;
			while (read.hasNextInt()) {
				ar[colI][0] = read.nextInt();
				colI++;
			}
		} catch (FileNotFoundException e) {
			
		}
		
		columnQuickSort(ar, 0);
		//System.out.println(isSorted(ar));
		for (int i = 0; i < 50; i++)
			System.out.println(ar[i][0]);
		//for (int i = 50; i < 100; i++)
		//	System.out.println(ar[i][0]);
		//for (Integer[] row : ar)
		//	System.out.println(row[0]);
	}
	
	
	
	
	/*---------------------------------------------------------------------
	|  Method columnQuickSort(Integer[][] grid, int col)
	|
	|  Purpose:  Sorts a single column of a 2D Integer array in ascending
	|                order (with grid[0][col] being the lowest val),
	|                using the quicksort algorithm.
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
	private static void columnQuickSort(Integer[][] grid, int col) {
		// Call the recursive helper method with default indices:
		columnQuickSortHelper(grid, col, 0, grid.length-51);
	}
	
	/*---------------------------------------------------------------------
	|  Method columnQuickSortHelper(Integer[][] grid, int col, int start, int end)
	|
	|  Purpose:  Recursive helper method for the columnQuickSort() method.
	|
	|  Pre-condition:  col is a valid column index (0 <= col < grid[0].length).
	|                      start and end are in range (0 <= start <= end <= [NUMROWS]).
	|
	|  Post-condition: This subset of the "col"th column will be sorted.
	|
	|  Parameters:
	|      Integer[][] grid -- the 2D array of Integers to sort a column of.
	|      int col -- the column of the grid to sort.
	|      int start -- the index to start at, inclusive.
	|      int end -- the index to end at, inclusive.
	|
	|  Returns:  None.
	*-------------------------------------------------------------------*/
	private static void columnQuickSortHelper(Integer[][] grid, int col, int start, int end) {
		// If col subset size <= 1, end:
		if (start >= end) {
			return;
		}
		// If only two elements, just sort simply:
		if (start == end-1) {
			if (grid[start][0] > grid[end][0]) {
				Integer temp = grid[start][0];
				grid[start][0] = grid[end][0];
				grid[end][0] = temp;
			}
			
			return;
		}
		
		// Get middle index as partition:
		int mid = (end-start) / 2;
		Integer partitionVal = grid[mid+start][col];
		
		// Move partition to front for ease of partitioning:
		grid[mid+start][col] = grid[start][col];
		grid[start][col] = partitionVal;
		
		// Partition the values:
		int startI = start+1;	// +1 to skip over partition value
		int endI = end;
		while (startI < endI) {
			// Find soonest too-large element with startI:
			while (startI < endI && grid[startI][col] <= partitionVal) {
				startI++;
			}
			
			// Find latest too-small element with endI:
			while (endI >= startI && grid[endI][col] >= partitionVal) {
				endI--;
			}

			// Swap the elements into the correct locations (if not done partitioning):
			if (startI < endI) {
				int temp = grid[startI][col];
				grid[startI][col] = grid[endI][col];
				grid[endI][col] = temp;
			
				// Increment indices, making sure they don't go out of bounds:
				if (startI == end || endI == start) {
					break;
				}
				startI++;
				endI--;
			}
		}
		
		//
		
		// Get the partition back to the middle:
		grid[start][col] = grid[endI][col];
		grid[endI][col] = partitionVal;
		
		System.out.println("Partition Val: " + partitionVal + ". " + start + "—" + end + " (" + mid + ")");
		for (int i = start; i <= end; i++) {
			System.out.println(grid[i][0]);
		}
		System.out.println();
		
		// Recursively quicksort:
		columnQuickSortHelper(grid, col, start, endI-1);
		columnQuickSortHelper(grid, col, startI-1, end);
	}
	
	public static boolean isSorted(Integer[][] grid) {
		for (int i = 50; i < grid.length-1; i++) {
			if (grid[i][0] > grid[i+1][0]) {
				return false;
			}
		}
		return true;
	}
	
	
//////////////////////////////////////////////////////////////////////////////////////////////
/*private static void columnQuickSortHelper(Integer[][] grid, int col, int start, int end) {
// If col subset size <= 1, end:
if (start >= end) {
return;
}
// If only two elements, just sort simply:
if (start == end-1) {
if (grid[start][col] > grid[end][col]) {
Integer temp = grid[start][col];
grid[start][col] = grid[end][col];
grid[end][col] = temp;
}

return;
}

// Get middle index as partition:
int mid = ((end-start) / 2) + start;
Integer partitionVal = grid[mid][col];

// Move partition to front for ease of partitioning:
grid[mid][col] = grid[start][col];
grid[start][col] = partitionVal;
//System.out.println(grid[start][col]);
// Partition the values:
int startI = start+1;	// +1 to skip over partition value
int endI = end;
while (startI < endI) {
// Find soonest too-large element with startI:
while (startI < endI && grid[startI][col] <= partitionVal &&
grid[endI][col] >= partitionVal) {
if (grid[startI][col] <= partitionVal)
startI++;
if (grid[endI][col] >= partitionVal)
endI--;
}
/*while (startI < endI && grid[startI][col] <= partitionVal) {
startI++;
}

// Find latest too-small element with endI:
while (endI > startI && grid[endI][col] >= partitionVal) {
endI--;
}

// Swap the elements into the correct locations (if not done partitioning):
if (startI < endI) {
if (grid[startI][col] > grid[endI][col]) {
int temp = grid[startI][col];
grid[startI][col] = grid[endI][col];
grid[endI][col] = temp;
}

// Increment indices, making sure they don't go out of bounds:
if (startI >= end || endI <= start) {
// Reset values to start/end if needed:
if (startI > end)
startI = end;
if (endI < start)
endI = start;
break;
}
startI++;
endI--;
}
} endI--;
if (endI != start && (grid[endI][col] > partitionVal || grid[endI+1][col] < partitionVal)) {
System.out.println(grid[endI][col] > partitionVal);
System.out.println(endI - start);
System.out.println(grid[endI-1][col]);
System.out.println(partitionVal);
System.out.println(grid[endI][col]);
System.out.println(grid[endI+1][col]);
System.exit(1);
}
//

// Get the partition back to the middle:
if (grid[start][col] > grid[start+1][col]) {
grid[start][col] = grid[endI][col];
grid[endI][col] = partitionVal;
}
/*System.out.println(endI);
System.out.println("Partition Val: " + partitionVal + ". " + start + "-" + end + " (" + mid + "), col " + col);
for (int i = start; i <= end; i++) {
System.out.println(grid[i][col]);
}
System.out.println();

// Recursively quicksort:
columnQuickSortHelper(grid, col, start, endI-1);
columnQuickSortHelper(grid, col, startI, end);
}*/
///////////////////////////////////////////////////////////////////////////////////
}
/*private static boolean notGarbage(Integer[][] grid) {
	for (int i = 0; i < (grid.length*grid[0].length)-1; i++) {
		if (readCMO(grid, i) > readCMO(grid, i+1)) {
			return false;
		}
	}
	return true;
}*/