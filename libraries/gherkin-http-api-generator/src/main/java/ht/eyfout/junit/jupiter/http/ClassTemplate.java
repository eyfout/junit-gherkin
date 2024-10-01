package ht.eyfout.junit.jupiter.http;


import ht.eyfout.junit.jupiter.api.http.HttpAPI;
import ht.eyfout.junit.jupiter.api.http.HttpAPIRequestBuilder;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

class ClassTemplate {
    private static final Type HTTP_API_REQUEST_BUILDER = Type.getType(HttpAPIRequestBuilder.class);
    private static final Type HTTP_API = Type.getType(HttpAPI.class);

    public final String name;
    public final Type type;
    private final String ns;
    private ClassNode node;

    public ClassTemplate(String ns, String name) {
        this.name = name;
        this.ns = ns;
        this.type = Type.getType((ns + "." + name).replace('.', '/'));
    }

    protected Type superType() {
        return Type.getType(Object.class);
    }

    protected Optional<Collection<Type>> interfaces() {
        return Optional.empty();
    }

    protected Collection<Type> annotations() {
        return Collections.emptyList();
    }

    ClassNode asmNode() {
        if (node == null) {
            node = new ClassNode(Opcodes.V1_5);
            node.name = type.getInternalName();
            node.access = Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL;
            node.superName = superType().getInternalName();
            node.interfaces = interfaces().orElseGet(Collections::emptyList)
                    .stream()
                    .map(Type::getInternalName).toList();
        }
        return node;
    }

    static class RequestBuilderClassTemplate extends ClassTemplate {
        public RequestBuilderClassTemplate(String pkg, String name) {
            super(pkg, name + "RequestBuilder");
        }

        @Override
        protected Type superType() {
            return HTTP_API_REQUEST_BUILDER;
        }

        @Override
        protected Collection<Type> annotations() {

            return super.annotations();
        }
    }

    static class HttpAPIClassTemplate extends ClassTemplate {
        public HttpAPIClassTemplate(String pkg, String name) {
            super(pkg, name + "HttpAPI");
        }

        @Override
        protected Optional<Collection<Type>> interfaces() {
            return Optional.of(List.of(HTTP_API));
        }
    }

    static class HttpParamClassTemplate extends ClassTemplate {
        public HttpParamClassTemplate(ClassTemplate parent, String name) {
            super(parent.ns, parent.name + "$In" + name);
            parent.asmNode().visitInnerClass(name, type.getInternalName(), type.getInternalName(), Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC);
        }
    }
}
