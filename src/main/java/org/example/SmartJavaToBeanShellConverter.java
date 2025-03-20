package org.example;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class SmartJavaToBeanShellConverter {
    private static final Map<String, String> API_NAME_MAPPING = new HashMap<>();
    static {
        API_NAME_MAPPING.put("sendmsg", "sendMsg");
        API_NAME_MAPPING.put("toast", "Toast");
        API_NAME_MAPPING.put("additem", "AddItem");
        API_NAME_MAPPING.put("getstring", "getString");
        API_NAME_MAPPING.put("putstring", "putString");
    }

    private static final Map<String, String> GLOBAL_VAR_MAPPING = new HashMap<>();
    static {
        GLOBAL_VAR_MAPPING.put("getMyUin()", "MyUin");
    }

    private static final String DEFAULT_INPUT = "src/main/java/org/example/MyQStoryScript.java";
    private static final String DEFAULT_OUTPUT = "output/main.java";

    public static void main(String[] args) {
        String inputFile = DEFAULT_INPUT;
        String outputFile = DEFAULT_OUTPUT;

        if (args.length == 2) {
            inputFile = args[0];
            outputFile = args[1];
        } else if (args.length != 0) {
            System.out.println("Usage: java SmartJavaToBeanShellConverter <input.java> <output.java>");
            return;
        } else {
            System.out.println("Using default paths:");
            System.out.println("Input: " + DEFAULT_INPUT);
            System.out.println("Output: " + DEFAULT_OUTPUT);
        }

        try {
            File outputDir = new File(outputFile).getParentFile();
            if (outputDir != null && !outputDir.exists()) {
                outputDir.mkdirs();
            }
            convert(inputFile, outputFile);
        } catch (IOException e) {
            System.err.println("Conversion failed: " + e.getMessage());
        }
    }

    public static void convert(String inputFile, String outputFile) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(inputFile)));
        CompilationUnit cu = StaticJavaParser.parse(content);

        StringBuilder imports = new StringBuilder();
        cu.getImports().forEach(imp -> imports.append(imp.toString()).append("\n"));

        StringBuilder scriptBody = new StringBuilder();
        cu.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(MethodDeclaration n, Void arg) {
                String methodSignature = adjustMethodSignature(n);
                String methodBody = n.getBody().map(body -> body.toString()).orElse("");
                methodBody = replaceGlobalVariables(methodBody);
                methodBody = fixApiMethodNames(methodBody);
                scriptBody.append(methodSignature).append("\n")
                        .append(methodBody).append("\n\n");
                super.visit(n, arg);
            }
        }, null);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            if (imports.length() > 0) {
                writer.write(imports.toString() + "\n");
            }
            writer.write(scriptBody.toString());
        }

        System.out.println("Conversion completed: " + outputFile);
    }

    private static String adjustMethodSignature(MethodDeclaration method) {
        String name = method.getNameAsString();
        String params = method.getParameters().toString()
                .replace("[", "").replace("]", "")
                .replace("Msg ", "Object ");

        String signature = method.getType() + " " + name + "(" + params + ")";
        signature = signature.replaceAll("\\b(public|private|protected|static|final)\\b\\s+", "");

        if (name.equals("onMsg")) {
            signature = "void onMsg(Object msg)";
        } else if (name.equals("加载提示")) {
            signature = "void 加载提示(String groupUin, String uin, int chatType)";
        }

        return signature;
    }

    private static String replaceGlobalVariables(String body) {
        for (Map.Entry<String, String> entry : GLOBAL_VAR_MAPPING.entrySet()) {
            body = body.replace(entry.getKey(), entry.getValue());
        }
        return body;
    }

    private static String fixApiMethodNames(String body) {
        for (Map.Entry<String, String> entry : API_NAME_MAPPING.entrySet()) {
            body = body.replaceAll("\\b" + entry.getKey() + "\\b", entry.getValue());
        }
        return body;
    }
}