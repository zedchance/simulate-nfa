/*
CSC 135 NFA simulator - Feb 4, 2021 - report errors tdk@csus.edu

This program reads from standard input a legal NFA without lambda
transitions in the format described at
http://ivanzuzak.info/noam/webapps/fsm_simulator/
and prints whether the string is accepted or rejected.
The line before the description starts is the input to be tested.
It is not a robust program. It requires:
- The NFA be legally constructed and have no epsilon/lambda arrows
- The order of the sections is exactly that shown at the website
  (#states, #initial, #accepting, #alphabet, #transitions)
- Transition lines are not allowed to use comma notation
- There is no whitespace in the file (except 1 newline after each line)
*/

import java.util.*;

public class Nfa
{
    public static final int SZ = 10;

    public static void main(String[] args)
    {
        // transition.get(s).get(c) is the Set of states with arrows from s
        // labelled c. Beware: If no arrows exist, null is returned
        Map<String, Map<String, Set<String>>> transition = new HashMap<>();

        // Grab first line as NFA's input
        Scanner in = new Scanner(System.in);
        String input = in.nextLine();

        // Read states and insert blank entry for each in transitions
        String tmp = in.nextLine();  // Should be "#states"
        tmp = in.nextLine();         // Should be first state
        while (!tmp.equals("#initial"))
        {
            transition.put(tmp, new HashMap<String, Set<String>>());
            tmp = in.nextLine();
        }

        // Get the start state
        String startState = in.nextLine();

        // Build set of accepting states
        Set<String> acceptStates = new HashSet<>();
        tmp = in.nextLine();   // Should be "#accepting"
        tmp = in.nextLine();   // Should be first accepting state
        while (!tmp.equals("#alphabet"))
        {
            acceptStates.add(tmp);
            tmp = in.nextLine();
        }

        // Skip over alphabet to "#transitions", add transitions to end-of-input
        while (!tmp.equals("#transitions"))
            tmp = in.nextLine();
        while (in.hasNextLine())
        {
            tmp = in.nextLine();
            int colon = tmp.indexOf(":");
            int greater = tmp.indexOf(">");
            String src = tmp.substring(0, colon);
            String label = tmp.substring(colon + 1, greater);
            String dst = tmp.substring(greater + 1);
            if (!transition.get(src).containsKey(label))
                transition.get(src).put(label, new HashSet<String>());
            transition.get(src).get(label).add(dst);
        }

        // Call simulator and report result
        if (recognizes(transition, startState, acceptStates, input))
            System.out.println("'" + input + "' is accepted");
        else
            System.out.println("'" + input + "' is rejected");
    }

    private static boolean recognizes(
            Map<String, Map<String, Set<String>>> transition,
            String startState,
            Set<String> acceptStates,
            String input)
    {
        Set<String> curStates = new HashSet<>(); // Holds states NFA could be in
        curStates.add(startState);               // Initially only the start state

        // for each char in input
        for (int i = 0; i < input.length(); i++)
        {
            // new HashSet for our next current states
            Set<String> nextCurStates = new HashSet<>();

            // convert char to String
            String chStr = Character.toString(input.charAt(i));

            // for each state in curStates
            for (String state : curStates)
            {
                // check if the transition is null
                if (transition.get(state).get(chStr) != null)
                {
                    // nextCurStates = nextCurStates union transition(state,ch)
                    nextCurStates.addAll(transition.get(state).get(chStr));
                }
            }
            curStates = nextCurStates;
        }

        // reject if curStates intersect acceptStates is empty
        curStates.retainAll(acceptStates);
        return !curStates.isEmpty();
    }
}
