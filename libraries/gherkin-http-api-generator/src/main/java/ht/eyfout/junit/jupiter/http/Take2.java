package ht.eyfout.junit.jupiter.http;

import ht.eyfout.junit.jupiter.http.generated.GherkinJUnitHttpAPI;
import ht.eyfout.junit.jupiter.http.generated.GherkinJUnitRequestBuilder;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.Paths;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.ClassNode;

import java.net.URL;
import java.util.Objects;
import java.util.Optional;

public class Take2 {

    static String replacement(Class<?> klass, String alt) {
        return klass.getSimpleName().replace(PREFIX, alt);
    }

    static String replace(String orig, Class<?> klass, String alt) {
        return orig.replace(klass.getSimpleName(), replacement(klass, alt));
    }

    static String replace(String orig, String alt) {
        if (Objects.isNull(orig)) {
            return orig;
        } else {
            String value = replace(
                    replace(
                            orig,
                            GherkinJUnitRequestBuilder.class,
                            alt),
                    GherkinJUnitHttpAPI.class,
                    alt);
            return replace(
                    replace(
                            replace(value, GherkinJUnitRequestBuilder.GherkinJUnitPathParam.class, alt),
                            GherkinJUnitRequestBuilder.GherkinJUnitQueryParam.class,
                            alt),
                    GherkinJUnitRequestBuilder.GherkinJUnitHeaderParam.class,
                    alt);
        }
    }

    static final String PREFIX = "GherkinJUnit";

    static String classCase(String name) {
        return name.toUpperCase().charAt(0) + name.substring(1);
    }

    static class RewriteHttpAPI extends ClassVisitor {

        private final Operation op;
        private final String replacement;
        private final ClassNode node;


        public RewriteHttpAPI(int api, Operation op) {
            super(api);
            this.op = op;
            this.replacement = classCase(op.getOperationId());
            this.node = new ClassNode(api);
        }


        @Override
        public ModuleVisitor visitModule(String name, int access, String version) {
            node.visitModule(replace(name, replacement), access, version);
            return super.visitModule(name, access, version);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            node.visitMethod(access, replace(name, replacement), desc, replace(signature, replacement), exceptions);
            return super.visitMethod(access, name, desc, signature, exceptions);
        }

        @Override
        public void visitEnd() {
            node.visitEnd();
            super.visitEnd();
        }

        @Override
        public void visitAttribute(Attribute attr) {
            node.visitAttribute(attr);
            super.visitAttribute(attr);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            node.visit(version, access, replace(name, replacement), replace(signature, replacement), replace(superName, replacement), interfaces);
            super.visit(version, access, name, signature, superName, interfaces);
        }
    }

    static public void generate(URL in) {
        OpenAPI openAPI = new OpenAPIParser().readLocation(in.toString(), null, null)
                .getOpenAPI();
        Paths paths = openAPI.getPaths();
        paths.forEach((key, pathItem) -> {
            Optional<Operation> operation = pathItem.readOperations().stream().findFirst();
            operation.ifPresent(op -> {
                RewriteHttpAPI apiRewriter = new RewriteHttpAPI(Opcodes.ASM4, op);
                try {
                    final Class<GherkinJUnitHttpAPI> api = GherkinJUnitHttpAPI.class;
                    ClassReader reader = new ClassReader(api.getSimpleName());
                    reader.accept(apiRewriter, Opcodes.V1_5);
                    ClassWriter writer = new ClassWriter(Opcodes.ASM4);
                    apiRewriter.node.accept(writer);
                    writer.toByteArray();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            });
            return;
        });
    }
}
