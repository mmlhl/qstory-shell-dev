package org.example;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class SmartJavaToBeanShellConverter {
    private static final Map<String, String> API_NAME_MAPPING = new HashMap<>();
    static {
        API_NAME_MAPPING.put("sendmsg", "sendMsg");
        API_NAME_MAPPING.put("toast", "Toast");
        API_NAME_MAPPING.put("additem", "AddItem");
        API_NAME_MAPPING.put("getstring", "getString");
        API_NAME_MAPPING.put("putstring", "putString");
        API_NAME_MAPPING.put("sendreply", "sendReply");
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
        Map<String, Set<String>> methodClassMap = new HashMap<>(); // 方法名 -> 类名集合，检测冲突

        // 第一步：收集所有类名和方法名
        for (File file : scriptDir.listFiles((dir, name) -> name.endsWith(".java"))) {
            String content = new String(Files.readAllBytes(file.toPath()));
            CompilationUnit cu = StaticJavaParser.parse(content);

            cu.accept(new VoidVisitorAdapter<Void>() {
                @Override
                public void visit(ClassOrInterfaceDeclaration n, Void arg) {
                    String className = n.getNameAsString();
                    for (MethodDeclaration method : n.getMethods()) {
                        String methodName = method.getNameAsString();
                        methodClassMap.computeIfAbsent(methodName, k -> new HashSet<>()).add(className);
                    }
                    super.visit(n, arg);
                }
            }, null);
        }

        // 检查冲突并打印警告
        for (Map.Entry<String, Set<String>> entry : methodClassMap.entrySet()) {
            if (entry.getValue().size() > 1) {
                System.err.println("Warning: Method name conflict detected for '" + entry.getKey() + "' in classes: " + entry.getValue());
            }
        }

        // 第二步：处理每个文件
        for (File file : scriptDir.listFiles((dir, name) -> name.endsWith(".java"))) {
            String content = new String(Files.readAllBytes(file.toPath()));
            CompilationUnit cu = StaticJavaParser.parse(content);

            // 收集 imports
            cu.getImports().forEach(imp -> imports.add(imp.toString()));

            // 使用 AST 修改器移除静态方法调用中的类名
            cu.accept(new ModifierVisitor<Void>() {
                @Override
                public MethodCallExpr visit(MethodCallExpr n, Void arg) {
                    if (n.getScope().isPresent()) {
                        String scope = n.getScope().get().toString();
                        String methodName = n.getNameAsString();
                        if (methodClassMap.containsKey(methodName) && methodClassMap.get(methodName).contains(scope)) {
                            // 如果无冲突，移除类名；有冲突时保留，后续加前缀
                            if (methodClassMap.get(methodName).size() == 1) {
                                n.setScope(null);
                            }
                        }
                    }
                    super.visit(n, arg);
                    return n;
                }
            }, null);

            // 处理类和方法
            cu.accept(new VoidVisitorAdapter<Void>() {
                @Override
                public void visit(ClassOrInterfaceDeclaration n, Void arg) {
                    String className = n.getNameAsString();
                    for (MethodDeclaration method : n.getMethods()) {
                        String methodName = method.getNameAsString();
                        // 如果有冲突，使用类名_方法名；无冲突或特殊方法，使用原名
                        String uniqueMethodName = (methodClassMap.get(methodName).size() > 1 && !isSpecialMethod(methodName))
                                ? className + "_" + methodName
                                : methodName;

                        String methodSignature = adjustMethodSignature(uniqueMethodName, method);
                        String methodBody = method.getBody().map(body -> body.toString()).orElse("");
                        methodBody = replaceGlobalVariables(methodBody);
                        methodBody = fixApiMethodNames(methodBody);
                        scriptBody.append(methodSignature).append("\n")
                                .append(methodBody).append("\n\n");
                    }
                    super.visit(n, arg);
                }
            }, null);
        }

        // 写入文件
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (String imp : imports) {
                writer.write(imp + "\n");
            }
            if (!imports.isEmpty()) writer.write("\n");
            writer.write(scriptBody.toString());
        }

        System.out.println("Conversion completed: " + outputFile);
    }

    private static String adjustMethodSignature(String methodName, MethodDeclaration method) {
        String name = method.getNameAsString();
        String params = method.getParameters().toString()
                .replace("[", "").replace("]", "")
                .replace("org.example.sdk.Msg ", "Object ");

        String signature = method.getType() + " " + methodName + "(" + params + ")";
        signature = signature.replaceAll("\\b(public|private|protected|static|final)\\b\\s+", "");

        if (name.equals("onMsg")) {
            signature = "void onMsg(Object msg)";
        } else if (name.equals("加载提示")) {
            signature = "void 加载提示(String groupUin, String uin, int chatType)";
        } else if (name.equals("init")) {
            signature = "void init()";
        }

        return signature;
    }

    private static boolean isSpecialMethod(String methodName) {
        return methodName.equals("init") || methodName.equals("onMsg") || methodName.equals("加载提示");
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