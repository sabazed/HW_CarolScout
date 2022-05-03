package pgdp.carol;

import java.util.Arrays;

import static pgdp.MiniJava.printPlayground;
import static pgdp.MiniJava.write;

public class CarolPfadfinder {

    public static void main(String[] args) {

        // Note that in this array initialization the rows are in reverse order and both
        // x- and y-axis are swapped.
        int[][] playground = { //
                { 0, 0, 0, 0, 0, 9 }, //
                { 0, 0, 0, 0, 0, 9 }, //
                { 9, 9, 9, 7, 9, 9 }, //
                { 9, 0, 0, 0, 0, 0 }, //
        };
        // [s, s, s, r, s, n, n, n, n, n, n, s, s, l, s, s] 130
        //

        int startX = 0;
        int startY = 0;
        int startDir = 1;
        int startBlocks = 0;

        printPlayground(playground, startX, startY, startDir, startBlocks);

        int findX = 3;
        int findY = 5;

//        int[][] playground = { //
//                {0, -1, -1, 2},
//        };
//        int startX = 0;
//        int startY = 2;
//        int startDir = 1;
//        int startBlocks = 1;
//
//        printPlayground(playground, startX, startY, startDir, startBlocks);
//
//        int findX = 0;
//        int findY = 3;

        long startTime = System.nanoTime();
        // this is expected to have an optimal solution with exactly 40 instructions
        char[] instructions = null;
		instructions = findOptimalSolution(playground, startX, startY, startDir, startBlocks, findX, findY, 16);
        boolean success = instructions != null;
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        System.out.println("Execution time in milliseconds: " + timeElapsed / 1000000);

        if (success) {
            write("SUCCESS");
            printPlayground(playground);
            write(Arrays.toString(instructions));
        } else {
            write("FAILED");
        }
    }

    // For the tests
    public static boolean lastTurnsAreUseless(char[] instr, int filled) {
        if (filled < 0 || instr.length == 0) return false;
        // Check for patterns: l/r followed by r/l or three l/r in a row
        // Safety check
        if (filled <= instr.length) {
            if (filled > 1) {
                if (instr[filled - 1] == 'r') {
                    if (instr[filled - 2] == 'l' || instr[filled - 2] == 'r') return true;
                    else if (filled > 2) {
                        if (instr[filled - 3] == 'l' || instr[filled - 3] == 'r') return true;
                    }
                } else if (instr[filled - 1] == 'l') {
                    if (instr[filled - 2] == 'r') return true;
                    else if (filled > 2) {
                        if (instr[filled - 3] == 'r' || instr[filled - 3] == 'l') return true;
                    }
                }
                // Check for patterns: n/p followed by p/n
                else if (filled > 2) {
                    if (instr[filled - 1] == 'n' && instr[filled - 2] == 'p') return true;
                    else if (instr[filled - 1] == 'p' && instr[filled - 2] == 'n') return true;
                }
            }
        }
        return false;
    }

    // For the tests
    static boolean wasThereBefore(char[] instr, int filled) {
        if (filled == 0 || instr.length == 0) return false;
            // If the last one is turning instruction return true
        else if (instr[filled - 1] == 'l' || instr[filled - 1] == 'r') return true;
            // If the last one is an ice placement return false
        else if (instr[filled - 1] == 'n' || instr[filled - 1] == 'p') return false;
            // Determine if the last instruction changes the position
        else if (instr[filled - 1] == 's') {
            // Save all the possible positions
            int[][] moves = new int[filled + 1][3];
            moves[0][0] = 0;
            moves[0][1] = 0;
            moves[0][2] = 0;
            int x = 0;
            int y = 0;
            int dir = 0;
            // Create a variable to determine block placement
            int ice = 0;
            for (int i = 1; i <= filled; i++) {
                // Make a next step
                if (instr[i - 1] == 's') {
                    if (dir == 0) x++;
                    else if (dir == 1) y++;
                    else if (dir == 2) x--;
                    else y--;
                }

                // Turn left
                else if (instr[i - 1] == 'l') {
                    if (dir != 3) dir++;
                    else dir = 0;
                }

                // Turn right
                else if (instr[i - 1] == 'r') {
                    if (dir != 0) dir--;
                    else dir = 3;
                }

                // Put a block or take it
                else if (instr[i - 1] == 'n') ice--;
                else if (instr[i - 1] == 'p') ice++;

                // Update the moves array
                moves[i][0] = x;
                moves[i][1] = y;
                moves[i][2] = ice;
            }

            // Compare elements and check if there has been any ice placement changes between them
            for (int i = 0; i < filled; i++) {
                for (int j = i + 1; j <= filled; j++) {
                    if (moves[i][0] == moves[j][0] && moves[i][1] == moves[j][1] && moves[i][2] == moves[j][2]) {
                        if (instr[i] != 'r' && instr[i] != 'l') {
                            // Return false if there are any ice placements;
                            for (int k = i + 1; k < j; k++) {
                                if (instr[k - 1] == 'n' || instr[k - 1] == 'p') return false;
                            }
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    // Check if any piece of instructions can be shortened
    public static boolean lastTurnsAreNotUseful(char[] instr, int filled) {
        // Get the max index
        int ln = instr.length - 1;
        if (filled < 1 || ln < 1 || filled > ln) return false;
        // Check for patterns: l/r followed by r/l or three l/r in a row
        // Safety check
        if (instr[filled] == 'r' && instr[filled - 1] == 'l') return true;
        else if (instr[filled] == 'l' && instr[filled - 1] == 'r') return true;
        else if (instr[filled] == 'n' && instr[filled - 1] == 'p') return true;
        else if (instr[filled] == 'p' && instr[filled - 1] == 'n') return true;
        else if (filled > 1) {
            if (instr[filled] == 'r' && instr[filled - 1] == 'r' && instr[filled - 2] == 'r') return true;
            else if (instr[filled] == 'l' && instr[filled - 1] == 'l' && instr[filled - 2] == 'l') return true;
        }
        return false;
    }

    // Determine if any piece of instructions is useless
    static boolean wasThereAlready(char[] instr, int filled) {
        // Get the max index
        int len = instr.length - 1;
        if (filled < 1 || len < 1 || filled > len) return false;
        // If the last one is an ice placement return false
        char lastone = instr[filled];
        if (lastone == 'n' || lastone == 'p') return false;
        // Save all the possible positions
        int[][] moves = new int[filled + 2][2];
        int x = 0;
        int y = 0;
        int dir = 0;
        moves[filled + 1][0] = x;
        moves[filled + 1][1] = y;
        for (int i = filled; i >= 0; i--) {
            // Make a next step
            if (instr[i] == 's') {
                if (dir == 0) x++;
                else if (dir == 1) y++;
                else if (dir == 2) x--;
                else y--;
            }
            // Turn left
            else if (instr[i] == 'l') {
                if (dir != 3) dir++;
                else dir = 0;
            }
            // Turn right
            else if (instr[i] == 'r') {
                if (dir != 0) dir--;
                else dir = 3;
            }
            // Compare elements and check if there has been any ice placement changes between them
            for (int j = filled - 1; j > i; j--) {
                if (moves[i][0] == moves[j][0] && moves[i][1] == moves[j][1]) {
                    if (instr[i] != 'r' && instr[i] != 'l') {
                        for (int k = j; k < i; k--) {
                            if (instr[k] == 'n' || instr[k] == 'p') return false;
                        }
                        return true;
                    }
                }
            }

            // Update the moves array
            moves[i][0] = x;
            moves[i][1] = y;
        }
        return false;
    }

    // Calculate minimal steps for getting to the target position
    static int getMinimalStepsAndTurns(int x, int y, int direction, int findX, int findY) {
        int steps = 0;
        if (x == findX && y == findY) {
            return steps;
        }
        // Only cases when the first turn is needed twice
        else if (x == findX) {
            if (y > findY) {
                // Pure steps
                steps = y - findY;
                if (direction == 3) steps += 2;
                else if (direction != 1) steps++;
            } else {
                steps = findY - y;
                if (direction == 1) steps += 2;
                else if (direction != 3) steps++;
            }
        } else if (y == findY) {
            if (x > findX) {
                // Pure steps
                steps = x - findX;
                if (direction == 0) steps += 2;
                else if (direction != 2) steps++;
            } else {
                steps = findX - x;
                if (direction == 2) steps += 2;
                else if (direction != 0) steps++;
            }
        }

        // Cases when only 1 turn is needed max
        else if (x > findX && y > findY) {
            steps = (x - findX) + (y - findY);
            // Direction 2 and 3 for shortest path
            if (direction == 2 || direction == 3) {
                steps += 1;
            } else {
                steps += 2;
            }
        } else if (x < findX && y > findY) {
            steps = (findX - x) + (y - findY);
            // Direction 0 and 3 for shortest path
            if (direction == 0 || direction == 3) {
                steps += 1;
            } else {
                steps += 2;
            }
        } else if (x > findX && y < findY) {
            steps = (x - findX) + (findY - y);
            // Direction 1 and 2 for shortest path
            if (direction == 1 || direction == 2) {
                steps += 1;
            } else {
                steps += 2;
            }
        } else if (x < findX && y < findY) {
            steps = (findX - x) + (findY - y);
            // Direction 0 and 1 for shortest path
            if (direction == 0 || direction == 1) {
                steps += 1;
            } else {
                steps += 2;
            }
        }
        return steps;
    }

    public static char[] findOptimalSolution(int[][] playground, int x, int y, int dir, int blocks, int fX, int fY, int searchlimit) {
        // Try the path-finding algorithm on minimal instruction amounts
        for (int limit = getMinimalStepsAndTurns(x, y, dir, fX, fY); limit <= searchlimit; limit++) {
            char[] instructions = new char[limit];
            if (findInstructions(playground, x, y, dir, blocks, fX, fY, instructions)) return instructions;
        }
        // If the searchLimit isn't enough then return false
        return null;
    }

    // Find the instructions
    public static boolean findInstructions(int[][] playground, int x, int y, int direction, int blocks, int findX, int findY, char[] instructions) {
        // Check if the destination is already reached
        if (x == findX && y == findY) {
            for (int i = 0; i < instructions.length; i++) instructions[i] = 'e';
            return true;
        }

        // Find max amount of instructions
        int max = instructions.length;
        // Create an array to store the instructions
        char[] paths = new char[max];
        // If solution is found then copy all its content to instructions array
        if (pathFinder(playground, x, y, direction, blocks, findX, findY, paths, max - 1, 0)) {
            for (int i = 0; i < max; i++) {
                instructions[i] = paths[i];
            }
            return true;
        }
        // Return false if the path is not found
        return false;
    }

    // Create a path-finder function for the sake of saving time
    public static boolean pathFinder(int[][] playground, int x, int y, int dir, int blocks, int fX, int fY, char[] instr, int max, int filled) {
//        Uncomment the lines bellow to print the searching process (takes much time)
//        printPlayground(playground, x, y, dir, blocks);
//        System.out.println(instr);
        // Check if the destination is reached
        if (x == fX && y == fY) {
            for (int i = filled; i <= max; i++) instr[i] = 'e';
            return true;
        }

        // Check if the path is useless
        // filled - 1 because we need the index of the previous cycle
        if (lastTurnsAreNotUseful(instr, filled - 1) || wasThereAlready(instr, filled - 1)) return false;

        // Check if the filled is more than max
        if (filled > max) return false;

        // Save playground max coordinates
        int mx = playground.length;
        int my = playground[0].length;

        // Call the function for every possible instruction
        //////////////////////////////////////////////////////
        // 's'
        //////////////////////////////////////////////////////
        instr[filled] = 's';
        // Check it for all directions
        if (dir == 0 && x + 1 < mx) {
            // Calculate the height difference
            int difference = playground[x + 1][y] - playground[x][y];
            // Check if the next move is impossible (out of bounds or height problem)
            if (difference <= 1 && difference >= -1) {
                if (pathFinder(playground, x + 1, y, dir, blocks, fX, fY, instr, max, filled + 1)) return true;
            }
        }
        else if (dir == 1 && y + 1 < my) {
            // Check if the ice is a problem
            int difference = playground[x][y + 1] - playground[x][y];
            // Check if the next move is impossible (out of bounds or height problem)
            if (difference <= 1 && difference >= -1) {
                if (pathFinder(playground, x, y + 1, dir, blocks, fX, fY, instr, max, filled + 1)) return true;
            }
        }
        else if (dir == 2 && x - 1 >= 0) {
            // Check if the ice is a problem
            int difference = playground[x - 1][y] - playground[x][y];
            // Check if the next move is impossible (out of bounds or height problem)
            if (difference <= 1 && difference >= -1) {
                if (pathFinder(playground, x - 1, y, dir, blocks, fX, fY, instr, max, filled + 1)) return true;
            }
        }
        else if (dir == 3 && y - 1 >= 0) {
            // Check if the ice is a problem
            int difference = playground[x][y - 1] - playground[x][y];
            // Check if the next move is impossible (out of bounds or height problem)
            if (difference <= 1 && difference >= -1) {
                if (pathFinder(playground, x, y - 1, dir, blocks, fX, fY, instr, max, filled + 1)) return true;
            }
        }

        //////////////////////////////////////////////////////
        // 'r'
        //////////////////////////////////////////////////////
        instr[filled] = 'r';
        // Check for all directions
        if (dir == 0) {
            if (pathFinder(playground, x, y, 3, blocks, fX, fY, instr, max, filled + 1)) return true;
        }
        else if (dir == 1) {
            if (pathFinder(playground, x, y, 0, blocks, fX, fY, instr, max, filled + 1)) return true;
        }
        else if (dir == 2) {
            if (pathFinder(playground, x, y, 1, blocks, fX, fY, instr, max, filled + 1)) return true;
        }
        else if (dir == 3) {
            if (pathFinder(playground, x, y, 2, blocks, fX, fY, instr, max, filled + 1)) return true;
        }

        //////////////////////////////////////////////////////
        // 'l'
        //////////////////////////////////////////////////////
        instr[filled] = 'l';
        // Check for all directions
        if (dir == 0) {
            if (pathFinder(playground, x, y, 1, blocks, fX, fY, instr, max, filled + 1)) return true;
        }
        else if (dir == 1) {
            if (pathFinder(playground, x, y, 2, blocks, fX, fY, instr, max, filled + 1)) return true;
        }
        else if (dir == 2) {
            if (pathFinder(playground, x, y, 3, blocks, fX, fY, instr, max, filled + 1)) return true;
        }
        else if (dir == 3) {
            if (pathFinder(playground, x, y, 0, blocks, fX, fY, instr, max, filled + 1)) return true;
        }

        //////////////////////////////////////////////////////
        // 'n'
        //////////////////////////////////////////////////////
        instr[filled] = 'n';
        // Check if the current position isn't in the water
        if (playground[x][y] > -1) {
            // Check it for all directions
            if (dir == 0 && x + 1 < mx) {
                // Calculate the height of the next field
                int height = playground[x + 1][y];
                // Check if the next move is impossible (out of bounds, max blocks or height problem)
                if (blocks < 10 && height > -1) {
                    // Update playground for the sake of recursion
                    playground[x + 1][y] -= 1;
                    if (pathFinder(playground, x, y, dir, blocks + 1, fX, fY, instr, max, filled + 1)) return true;
                    // Undo the last update if the recursion fails
                    playground[x + 1][y] += 1;
                }
            }
            else if (dir == 1 && y + 1 < my) {
                // Calculate the height of the next field
                int height = playground[x][y + 1];
                // Check if the next move is impossible (out of bounds, max blocks or height problem)
                if (blocks < 10 && height > -1) {
                    // Update playground for the sake of recursion
                    playground[x][y + 1] -= 1;
                    if (pathFinder(playground, x, y, dir, blocks + 1, fX, fY, instr, max, filled + 1)) return true;
                    // Undo the last update if the recursion fails
                    playground[x][y + 1] += 1;
                }
            }
            else if (dir == 2 && x - 1 >= 0) {
                // Calculate the height of the next field
                int height = playground[x - 1][y];
                // Check if the next move is impossible (out of bounds, max blocks or height problem)
                if (blocks < 10 && height > -1) {
                    // Update playground for the sake of recursion
                    playground[x - 1][y] -= 1;
                    if (pathFinder(playground, x, y, dir, blocks + 1, fX, fY, instr, max, filled + 1)) return true;
                    // Undo the last update if the recursion fails
                    playground[x - 1][y] += 1;
                }
            }
            else if (dir == 3 && y - 1 >= 0) {
                // Calculate the height of the next field
                int height = playground[x][y - 1];
                // Check if the next move is impossible (out of bounds, max blocks or height problem)
                if (blocks < 10 && height > -1) {
                    // Update playground for the sake of recursion
                    playground[x][y - 1] -= 1;
                    if (pathFinder(playground, x, y, dir, blocks + 1, fX, fY, instr, max, filled + 1)) return true;
                    // Undo the last update if the recursion fails
                    playground[x][y - 1] += 1;
                }
            }
        }

        //////////////////////////////////////////////////////
        // 'p'
        //////////////////////////////////////////////////////
        instr[filled] = 'p';
        // Check if the current position isn't in the water
        if (playground[x][y] > -1) {
            // Check it for all directions
            if (dir == 0 && x + 1 < mx) {
                // Calculate the height of the next field
                int height = playground[x + 1][y];
                // Check if the next move is impossible (out of bounds or height problem)
                if (blocks > 0 && height < 9) {
                    // Update playground for the sake of recursion
                    playground[x + 1][y] += 1;
                    if (pathFinder(playground, x, y, dir, blocks - 1, fX, fY, instr, max, filled + 1)) return true;
                    // Undo the last update if the recursion fails
                    playground[x + 1][y] -= 1;
                }
            }
            else if (dir == 1 && y + 1 < my) {
                // Calculate the height of the next field
                int height = playground[x][y + 1];
                // Check if the next move is impossible (out of bounds or height problem)
                if (blocks > 0 && height < 9) {
                    // Update playground for the sake of recursion
                    playground[x][y + 1] += 1;
                    if (pathFinder(playground, x, y, dir, blocks - 1, fX, fY, instr, max, filled + 1)) return true;
                    // Undo the last update if the recursion fails
                    playground[x][y + 1] -= 1;
                }
            }
            else if (dir == 2 && x - 1 >= 0) {
                // Calculate the height of the next field
                int height = playground[x - 1][y];
                // Check if the next move is impossible (out of bounds or height problem)
                if (blocks > 0 && height < 9) {
                    // Update playground for the sake of recursion
                    playground[x - 1][y] += 1;
                    if (pathFinder(playground, x, y, dir, blocks - 1, fX, fY, instr, max, filled + 1)) return true;
                    // Undo the last update if the recursion fails
                    playground[x - 1][y] -= 1;
                }
            }
            else if (dir == 3 && y - 1 >= 0) {
                // Calculate the height of the next field
                int height = playground[x][y - 1];
                // Check if the next move is impossible (out of bounds or height problem)
                if (blocks > 0 && height < 9) {
                    // Update playground for the sake of recursion
                    playground[x][y - 1] += 1;
                    if (pathFinder(playground, x, y, dir, blocks - 1, fX, fY, instr, max, filled + 1)) return true;
                    // Undo the last update if the recursion fails
                    playground[x][y - 1] -= 1;
                }
            }
        }

        // If none of the instructions are available fill the place with 'e'
        instr[filled] = 'e';
        return false;
    }
}