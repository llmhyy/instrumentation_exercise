package sample;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.BranchInstruction;
import org.apache.bcel.generic.GotoInstruction;
import org.apache.bcel.generic.IfInstruction;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LOOKUPSWITCH;
import org.apache.bcel.generic.ReturnInstruction;
import org.apache.bcel.generic.TABLESWITCH;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuicksortCFG {

    // DOT file strings.
    private static final String[] fileHeader = new String[]{
            "Control Flow Graph for QuickSort :",
            "",
            " [shape] [rectangle],   [diamond],   [line]",
            "              ||            ||         ||",
            " [label] [statement], [conditional], [flow]",
            ""
    };

    private static final String entry = "entry";
    private static final String exit = "exit";

    /**
     * Constructor
     * Loads an instruction list.
     *
     * @param instructions Instruction list from the method to create the CFG from.
     */
//    public QuicksortCFG(InstructionList instructions) {
//        instructionHandleArray = instructions.getInstructionHandles();
//    }

    /**
     * Main method. Generate a DOT file with the CFG representing a given class file.
     *
     * @param args Expects two arguments: <input-class-file> <output-dot-file>
     */
    public static void main(String[] args) {

        // Check arguments.
        if (args.length != 2) {
            System.out.println("Wrong number of arguments.");
            System.out.println("Format: java CFG <input-class-file> <output-dot-file>");
            System.exit(1);
        }

        String inputClassFileName = args[0];
        String outputDotFileName = args[1];

        // Output DOT file.
        System.out.println("Generating DOT file...");

        generateCFG(inputClassFileName, outputDotFileName);

        System.out.println("Done.");
    }

    /**
     * Generate a CFG representing each method in given class file
     *
     * @param inputClassFileName
     * @param outputDotFileName
     */
    private static void generateCFG(String inputClassFileName, String outputDotFileName) {

        //Parse class file
        JavaClass javaClass = null;

        try {
            javaClass = (new ClassParser(inputClassFileName)).parse();
        } catch (IOException e) {
            System.out.println("Error while parsing " + inputClassFileName + ".");
            System.exit(1);
        }

        System.out.println("Creating CFG object...");

        // Search for methods.
        if (javaClass.getMethods() == null) {
            System.out.println("No method is found.");
            System.exit(1);
        }

        Method[] methods = javaClass.getMethods();

//      QuicksortCFG cfg = new QuicksortCFG(new InstructionList(methods[i].getCode().getCode()));

        try {
            OutputStream output = new FileOutputStream(outputDotFileName);
//          generateIns(output, new InstructionList(methods[i].getCode().getCode()), methods[i]);
            generateIns(output, methods);
            output.close();
        } catch (IOException e) {
            System.exit(1);
        }
    }

    /**
     * Generates a DOT file representing the CFG.
     *
     * @param out     OutputStream to write the DOT file to.
     * @param methods Method[] to representing given methods
     */
    private static void generateIns(OutputStream out, Method... methods) {

        Map<InstructionHandle, List<InstructionHandle>> instructionHandleMap = new HashMap<>();
        PrintStream printStream = new PrintStream(out);

        for (String s : fileHeader) {
            printStream.print(s);
            printStream.print("\n");
        }

        for (Method method : methods) {
            InstructionList instructions = new InstructionList(method.getCode().getCode());
            InstructionHandle[] instructionHandleArray = instructions.getInstructionHandles();

            printStream.print('\n');
            printStream.print("CFG for " + "'" + method.getName() + "'" + ":");
            printStream.print(" source code line number # " + (method.getCode().getLineNumberTable().getSourceLine(0) - 2));

            printStream.print('\n');
            printStream.print('\n');
            printStream.print(entry + " -> " + instructionHandleArray[0].getPosition());

            for (int i = 0; i < instructionHandleArray.length - 1; i++) {
                Instruction instruction = instructionHandleArray[i].getInstruction();

                int start, end;

                start = instructionHandleArray[i].getPosition();

                // Branch
                if (instruction instanceof BranchInstruction) {

                    //If
                    if (instruction instanceof IfInstruction) {
                        InstructionHandle target = ((IfInstruction) instruction).getTarget();
                        end = target.getPosition();

                        printStream.print(" ( " + instruction.getName() + " true " + ") ");
                        printStream.print(" -> " + end + " or");
                        printStream.print("\n");
                        printStream.print(start);

                        insert(instructionHandleArray[i], target, instructionHandleMap);

                        end = instructionHandleArray[i + 1].getPosition();
                        printStream.print(" -> " + end);

                        insert(instructionHandleArray[i], instructionHandleArray[i + 1], instructionHandleMap);
                    }

                    //GOTO
                    if (instruction instanceof GotoInstruction) {
                        InstructionHandle target = ((GotoInstruction) instruction).getTarget();
                        end = target.getPosition();
                        printStream.print(" -> " + end);
                        insert(instructionHandleArray[i], target, instructionHandleMap);
                    }

                    // Table switch
                    // O(1)
                    if (instruction instanceof TABLESWITCH) {

                        InstructionHandle handles[] = ((TABLESWITCH) instruction).getTargets();

                        for (int j = 0; j < handles.length; j++) {

                            end = handles[j].getPosition();
                            printStream.print(" -> " + end);
                            insert(instructionHandleArray[i], handles[j], instructionHandleMap);
                        }
                        InstructionHandle target = ((TABLESWITCH) instruction).getTarget();
                        end = target.getPosition();
                        printStream.print(" -> " + end);
                        insert(instructionHandleArray[i], target, instructionHandleMap);
                    }

                    // Lookup switch
                    // O(log N)
                    if (instruction instanceof LOOKUPSWITCH) {

                        InstructionHandle handles[] = ((LOOKUPSWITCH) instruction).getTargets();

                        for (int j = 0; j < handles.length; j++) {

                            end = handles[j].getPosition();
                            printStream.print(" -> " + end);
                            insert(instructionHandleArray[i], handles[j], instructionHandleMap);
                        }

                        InstructionHandle target = ((LOOKUPSWITCH) instruction).getTarget();
                        end = target.getPosition();
                        printStream.print(" -> " + end);
                        insert(instructionHandleArray[i], target, instructionHandleMap);
                    }


                } else {
                    //Return
                    if (instruction instanceof ReturnInstruction) {

                        printStream.print(" -> " + exit + ";");
                        printStream.print("\n");

                    } else {
                        // Others
                        end = instructionHandleArray[i + 1].getPosition();
                        printStream.print(" -> " + end);
                        insert(instructionHandleArray[i], instructionHandleArray[i + 1], instructionHandleMap);
                    }
                }
            }

//            int length = instructionHandleArray.length;
            printStream.print(" -> " + exit + ";");

            printStream.print("\n");
            printStream.print("\n");
            printStream.print("Method " + "'" + method.getName() + "'" + " done ");
            printStream.print("\n");
            printStream.print("\n");

        }
    }

    private static void insert(InstructionHandle startIns, InstructionHandle endIns, Map<InstructionHandle, List<InstructionHandle>> instructionHandleMap) {
        if (instructionHandleMap.containsKey(startIns)) {
            List<InstructionHandle> temp = instructionHandleMap.get(startIns);
            temp.add(endIns);
            instructionHandleMap.put(startIns, temp);

        } else {
            List<InstructionHandle> temp = new ArrayList<>();
            temp.add(endIns);
            instructionHandleMap.put(startIns, temp);
        }
    }
}