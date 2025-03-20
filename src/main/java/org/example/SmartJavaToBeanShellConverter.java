package org.example;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    private static final String SCRIPT_DIR = "src/main/java/org/example/script/";
    private static final String DEFAULT_OUTPUT = "output/main.java";

    public static void main(String[] args) {
        String outputFile = DEFAULT_OUTPUT;

        if (args.length == 1) {
            outputFile = args[0];
        } else if (args.length > 1) {
            System.out.println("Usage: java SmartJavaToBeanShellConverter [<output.java>]");
            return;
        }

        try {
            File outputDir = new File(outputFile).getParentFile();
            if (outputDir != null && !outputDir.exists()) {
                outputDir.mkdirs();
            }
            convertScripts(outputFile);
        } catch (IOException e) {
            System.err.println("Conversion failed: " + e.getMessage());
        }
    }

    public static void convertScripts(String outputFile) throws IOException {
        File scriptDir = new File(SCRIPT_DIR);
        Set<String> imports = new HashSet<>();
        StringBuilder scriptBody = new StringBuilder();

        for (File file : scriptDir.listFiles((dir, name) -> name.endsWith(".java"))) {
            String content = new String(Files.readAllBytes(file.toPath()));
            CompilationUnit cu = StaticJavaParser.parse(content);

            cu.getImports().forEach(imp -> imports.add(imp.toString()));

            cu.accept(new VoidVisitorAdapter<Void>() {
                @Override
                public void visit(ClassOrInterfaceDeclaration n, Void arg) {
                    String className = n.getNameAsString();
                    for (MethodDeclaration method : n.getMethods()) {
                        String methodSignature = adjustMethodSignature(className, method);
                        String methodBody = method.getBody().map(body -> body.toString()).orElse("");
                        methodBody = replaceGlobalVariables(methodBody);
                        methodBody = fixApiMethodNames(methodBody);
                        methodBody = replaceStaticMethodCalls(className, methodBody);
                        scriptBody.append(methodSignature).append("\n")
                                .append(methodBody).append("\n\n");
                    }
                    super.visit(n, arg);
                }
            }, null);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (String imp : imports) {
                writer.write(imp + "\n");
            }
            if (!imports.isEmpty()) writer.write("\n");
            writer.write(scriptBody.toString());
        }

        System.out.println("Conversion completed: " + outputFile);
    }

    private static String adjustMethodSignature(String className, MethodDeclaration method) {
        String name = method.getNameAsString();
        String params = method.getParameters().toString()
                .replace("[", "").replace("]", "")
                .replace("org.example.sdk.Msg ", "Object ");

        // 特殊处理 QStory 的保留方法名
        String methodName;
        if (name.equals("init") || name.equals("onMsg") || name.equals("加载提示")) {
            methodName = name; // 保留原名
        } else {
            methodName = method.isStatic() ? name : className + "_" + name; // 其他非静态方法加前缀
        }

        String signature = method.getType() + " " + methodName + "(" + params + ")";
        signature = signature.replaceAll("\\b(public|private|protected|static|final)\\b\\s+", "");

        // 调整 QStory 回调方法
        if (name.equals("onMsg")) {
            signature = "void onMsg(Object msg)";
        } else if (name.equals("加载提示")) {
            signature = "void 加载提示(String groupUin, String uin, int chatType)";
        } else if (name.equals("init")) {
            signature = "void init()";
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

    private static String replaceStaticMethodCalls(String className, String body) {
        return body.replaceAll(className + "\\.(\\w+)\\(", "$1(");
    }
}