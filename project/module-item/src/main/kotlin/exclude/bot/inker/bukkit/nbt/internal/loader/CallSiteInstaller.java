package exclude.bot.inker.bukkit.nbt.internal.loader;

import com.google.common.collect.ImmutableMap;
import exclude.bot.inker.bukkit.nbt.*;
import exclude.bot.inker.bukkit.nbt.internal.annotation.CbVersion;
import exclude.bot.inker.bukkit.nbt.internal.annotation.HandleBy;
import exclude.bot.inker.bukkit.nbt.loader.CallSiteNbt;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.PluginDescriptionFile;
import org.objectweb.asm.Type;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.MethodRemapper;
import org.objectweb.asm.commons.Remapper;
import sun.misc.Unsafe;

import java.io.*;
import java.lang.invoke.*;
import java.lang.reflect.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;

public final class CallSiteInstaller {
  private static final String CSN_PACKAGE;
  private static final String CSN_CLASS_FILE_PREFIX;
  private static final String CSN_LOADER_PACKAGE;
  private static final String CSN_LOADER_CLASS_FILE_PREFIX;
  private static final String CSN_INTERNAL_PACKAGE;
  private static final String CSN_INTERNAL_CLASS_FILE_PREFIX;
  private static final String CSN_REF_PACKAGE;
  private static final String CSN_REF_CLASS_FILE_PREFIX;
  private static final boolean ENABLE_DEBUG;
  private static Logger logger;
  private static boolean loaded = false;
  private static MethodHandles.Lookup lookup;
  private static TransformRemapper transformRemapper;
  private static Map<String, String> remapping;

  static {
    //noinspection StringOperationCanBeSimplified
    CSN_PACKAGE = new String("bot.inker.bukkit.nbt.");
    CSN_CLASS_FILE_PREFIX = CSN_PACKAGE.replace('.', '/');

    //noinspection StringOperationCanBeSimplified
    CSN_LOADER_PACKAGE = new String("bot.inker.bukkit.nbt.loader.");
    CSN_LOADER_CLASS_FILE_PREFIX = CSN_LOADER_PACKAGE.replace('.', '/');

    //noinspection StringOperationCanBeSimplified
    CSN_INTERNAL_PACKAGE = new String("bot.inker.bukkit.nbt.internal.");
    CSN_INTERNAL_CLASS_FILE_PREFIX = CSN_INTERNAL_PACKAGE.replace('.', '/');

    //noinspection StringOperationCanBeSimplified
    CSN_REF_PACKAGE = new String("bot.inker.bukkit.nbt.internal.ref.");
    CSN_REF_CLASS_FILE_PREFIX = CSN_REF_PACKAGE.replace('.', '/');

    ENABLE_DEBUG = Boolean.getBoolean(CSN_PACKAGE + "debug");
  }

  public static void install(Class<?> clazz) {
    if (!loaded) {
      try {
        installImpl(clazz);
      } catch (Exception e) {
        throw new IllegalStateException("Failed to install CallSiteNbt", e);
      }
      Class<?>[] classes = new Class<?>[]{
          Nbt.class, NbtByte.class, NbtByteArray.class, NbtCompound.class, NbtCollection.class, NbtCompound.class,
          NbtCraftItemComponent.class, NbtDouble.class, NbtEnd.class, NbtFloat.class, NbtInt.class, NbtIntArray.class,
          NbtItemStack.class, NbtList.class, NbtLong.class, NbtLongArray.class, NbtNumeric.class, NbtShort.class,
          NbtString.class, NbtType.class
      };
      for (Class<?> loadedClass : classes) {
        // ensure class loaded
        loadedClass.getName();
      }
      loaded = true;
    }
  }

  private static void installImpl(Class<?> clazz) throws ReflectiveOperationException, IOException, InvalidDescriptionException {
    Class<?> pluginClassLoaderClass = Class.forName("org.bukkit.plugin.java.PluginClassLoader");
    ClassLoader classLoader = clazz.getClassLoader();
    if (classLoader.getClass() != pluginClassLoaderClass) {
      throw new IllegalStateException("CallSiteNbt can only install in PluginClassLoader");
    }
    Field fileField = pluginClassLoaderClass.getDeclaredField("file");
    fileField.setAccessible(true);
    File pluginFile = (File) fileField.get(classLoader);
    Field jarField = pluginClassLoaderClass.getDeclaredField("jar");
    jarField.setAccessible(true);
    JarFile pluginJar = (JarFile) jarField.get(classLoader);
    DelegateJarFile delegateJarFile = new DelegateJarFile(pluginFile, pluginJar);
    PluginDescriptionFile pluginYaml;
    try (InputStream in = delegateJarFile.getInputStream(delegateJarFile.getEntry("plugin.yml"))) {
      pluginYaml = new PluginDescriptionFile(in);
    }
    logger = Logger.getLogger(pluginYaml.getName());
    logger.log(Level.INFO, "install callsite-nbt in " + CSN_PACKAGE);
    if (!pluginYaml.getName().equals("callsite-nbt") && CallSiteNbt.class.getName().equals(new String(new char[]{
        'b', 'o', 't', '.', 'i', 'n', 'k', 'e', 'r', '.', 'b', 'u', 'k',
        'k', 'i', 't', '.', 'n', 'b', 't', '.', 'l', 'o', 'a', 'd', 'e',
        'r', '.', 'C', 'a', 'l', 'l', 'S', 'i', 't', 'e', 'N', 'b', 't'
    }))) {
      logger.warning("You should relocate callsite nbt to your package");
    }
    jarField.set(classLoader, delegateJarFile);

    CallSiteInstaller.remapping = provideRemapping();
    CallSiteInstaller.lookup = provideLookup();
    CallSiteInstaller.transformRemapper = new TransformRemapper(new RefOnlyClassLoader(classLoader, pluginJar));
  }

  private static <T extends Throwable> Map<String, String> provideRemapping() throws T {
    String mappingContent = null;
    try {
      String currentVersion = CbVersion.current().name();

      mappingContent = (String) Class.forName("bot.inker.bukkit.nbt.internal.loader.ConstMappings")
          .getDeclaredField(currentVersion)
          .get(null);
    } catch (NoSuchFieldException e) {
      if (CbVersion.v1_17_R1.isSupport()) {
        throw (T) e;
      }
    } catch (IllegalAccessException | ClassNotFoundException e) {
      throw (T) e;
    }
    try {
      return mappingContent == null ? Collections.emptyMap() : provideRemapping(mappingContent);
    } catch (IOException e) {
      throw (T) e;
    }
  }

  private static Map<String, String> provideRemapping(String mappingContent) throws IOException {
    ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
    try (DataInputStream in = new DataInputStream(
        new GZIPInputStream(
            Base64.getDecoder().wrap(
                new StringBufferInputStream(mappingContent)
            )
        )
    )) {
      int constPoolSize = in.readInt();
      List<String> constPool = new ArrayList<>(constPoolSize);
      for (int i = 0; i < constPoolSize; i++) {
        constPool.add(in.readUTF());
      }
      int classNodeSize = in.readInt();
      Map<String, String> classNameMapping = new LinkedHashMap<>(classNodeSize);
      List<String> currentClassList = new ArrayList<>();
      for (int i = 0; i < classNodeSize; i++) {
        String currentClassName = constPool.get(in.readInt());
        String currentSpigotClassName = constPool.get(in.readInt());
        builder.put(currentClassName, currentSpigotClassName);
        classNameMapping.put(currentClassName, currentSpigotClassName);
        currentClassList.add(currentClassName);
      }
      for (int i = 0; i < classNodeSize; i++) {
        String currentClass = "L" + currentClassList.get(i) + ";";
        String targetClass = handleType(classNameMapping, Type.getType(currentClass)).getDescriptor();
        int fieldNodes = in.readInt();
        for (int j = 0; j < fieldNodes; j++) {
          String name = constPool.get(in.readInt());
          String type = constPool.get(in.readInt());
          String remapped = constPool.get(in.readInt());
          builder.put(
              currentClass + name + ":" + type,
              targetClass + remapped + ":" + handleType(classNameMapping, Type.getType(type)).getDescriptor()
          );
        }
        int methodNodes = in.readInt();
        for (int j = 0; j < methodNodes; j++) {
          String name = constPool.get(in.readInt());
          String returnType = constPool.get(in.readInt());
          StringBuilder sb = new StringBuilder();
          sb.append("(");
          int argsSize = in.readByte();
          for (int k = 0; k < argsSize; k++) {
            String argType = constPool.get(in.readInt());
            sb.append(argType);
          }
          sb.append(")").append(returnType);
          String remapped = constPool.get(in.readInt());
          builder.put(
              currentClass + name + sb,
              targetClass + remapped + handleType(
                  classNameMapping,
                  Type.getMethodType(sb.toString())
              ).getDescriptor()
          );
        }
      }
    }
    return builder.build();
  }

  private static MethodHandles.Lookup provideLookup() {
    try {
      Field theUnsafeField = Unsafe.class.getDeclaredField("theUnsafe");
      theUnsafeField.setAccessible(true);
      Unsafe unsafe = (Unsafe) theUnsafeField.get(null);

      Field implLookupField = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
      return (MethodHandles.Lookup) unsafe.getObject(
          unsafe.staticFieldBase(implLookupField),
          unsafe.staticFieldOffset(implLookupField)
      );
    } catch (Throwable e) {
      return null;
    }
  }

  private static byte[] transformBaseClass(byte[] input) {
    ClassReader reader = new ClassReader(input);
    ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
    ClassVisitor visitor = new TransformClassVisitor(writer, transformRemapper);
    reader.accept(visitor, 0);
    return writer.toByteArray();
  }

  private static byte[] readAllBytes(InputStream in) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream(Math.max(32, in.available()));
    byte[] buf = new byte[4096];
    int r;
    while ((r = in.read(buf)) != -1) {
      out.write(buf, 0, r);
    }
    return out.toByteArray();
  }

  private static String[] parseMethod(String reference) {
    String newReference = remapping.get(reference);
    boolean withFullRemap = (newReference != null);
    reference = withFullRemap ? newReference : reference;
    int firstSplitIndex = reference.indexOf(';');
    int secondSplitIndex = reference.indexOf('(', firstSplitIndex);
    Type ownerType = Type.getObjectType(reference.substring(1, firstSplitIndex));
    Type methodDesc = Type.getMethodType(reference.substring(secondSplitIndex));
    if (!withFullRemap) {
      ownerType = remapType(ownerType);
      methodDesc = remapType(methodDesc);
    }
    String[] result = new String[3];
    result[0] = parseType(ownerType).getInternalName();
    result[1] = reference.substring(firstSplitIndex + 1, secondSplitIndex);
    result[2] = parseType(methodDesc).getDescriptor();
    return result;
  }

  private static String[] parseField(String reference) {
    String newReference = remapping.get(reference);
    boolean withFullRemap = (newReference != null);
    reference = withFullRemap ? newReference : reference;
    int firstSplitIndex = reference.indexOf(';');
    int secondSplitIndex = reference.indexOf(':');
    Type ownerType = Type.getObjectType(reference.substring(1, firstSplitIndex));
    Type valueType = Type.getType(reference.substring(secondSplitIndex + 1));
    if (!withFullRemap) {
      ownerType = remapType(ownerType);
      valueType = remapType(valueType);
    }
    String[] result = new String[3];
    result[0] = parseType(ownerType).getInternalName();
    result[1] = reference.substring(firstSplitIndex + 1, secondSplitIndex);
    result[2] = parseType(valueType).getDescriptor();
    return result;
  }

  private static String parseClass(String reference) {
    reference = remapping.getOrDefault(reference, reference);
    return parseType(Type.getObjectType(reference)).getInternalName();
  }

  private static Type parseType(Type type) {
    switch (type.getSort()) {
      case Type.ARRAY:
        StringBuilder remappedDescriptor = new StringBuilder();
        for (int i = 0; i < type.getDimensions(); ++i) {
          remappedDescriptor.append('[');
        }
        remappedDescriptor.append(parseType(type.getElementType()).getDescriptor());
        return Type.getType(remappedDescriptor.toString());
      case Type.OBJECT:
        String matchedPrefix, internalName = type.getInternalName();
        if (internalName.startsWith("org/bukkit/craftbukkit/")) {
          matchedPrefix = "org/bukkit/craftbukkit/";
        } else if (!CbVersion.v1_17_R1.isSupport() && internalName.startsWith("net/minecraft/server/")) {
          matchedPrefix = "net/minecraft/server/";
        } else {
          return type;
        }
        int splitIndex = internalName.indexOf('/', matchedPrefix.length());
        return Type.getObjectType(matchedPrefix + CbVersion.current() + internalName.substring(splitIndex));
      case Type.METHOD:
        Type methodType = Type.getMethodType(type.getDescriptor());
        Type[] newArgumentTypes = new Type[methodType.getArgumentTypes().length];
        for (int i = 0; i < newArgumentTypes.length; i++) {
          newArgumentTypes[i] = parseType(methodType.getArgumentTypes()[i]);
        }
        return Type.getMethodType(parseType(methodType.getReturnType()), newArgumentTypes);
      default:
        return type;
    }
  }

  private static Type remapType(Type type) {
    switch (type.getSort()) {
      case Type.ARRAY:
        StringBuilder remappedDescriptor = new StringBuilder();
        for (int i = 0; i < type.getDimensions(); ++i) {
          remappedDescriptor.append('[');
        }
        remappedDescriptor.append(remapType(type.getElementType()).getDescriptor());
        return Type.getType(remappedDescriptor.toString());
      case Type.OBJECT:
        String internalName = type.getInternalName();
        return Type.getObjectType(remapping.getOrDefault(internalName, internalName));
      case Type.METHOD:
        Type methodType = Type.getMethodType(type.getDescriptor());
        Type[] newArgumentTypes = new Type[methodType.getArgumentTypes().length];
        for (int i = 0; i < newArgumentTypes.length; i++) {
          newArgumentTypes[i] = remapType(methodType.getArgumentTypes()[i]);
        }
        return Type.getMethodType(remapType(methodType.getReturnType()), newArgumentTypes);
      default:
        return type;
    }
  }

  private static Type handleType(Map<String, String> classMap, Type type) {
    switch (type.getSort()) {
      case Type.ARRAY:
        StringBuilder remappedDescriptor = new StringBuilder();
        for (int i = 0; i < type.getDimensions(); ++i) {
          remappedDescriptor.append('[');
        }
        remappedDescriptor.append(handleType(classMap, type.getElementType()).getDescriptor());
        return Type.getType(remappedDescriptor.toString());
      case Type.OBJECT:
        String internalName = type.getInternalName();
        return Type.getObjectType(classMap.getOrDefault(internalName, internalName));
      case Type.METHOD:
        Type methodType = Type.getMethodType(type.getDescriptor());
        Type[] newArgumentTypes = new Type[methodType.getArgumentTypes().length];
        for (int i = 0; i < newArgumentTypes.length; i++) {
          newArgumentTypes[i] = handleType(classMap, methodType.getArgumentTypes()[i]);
        }
        return Type.getMethodType(handleType(classMap, methodType.getReturnType()), newArgumentTypes);
      default:
        return type;
    }
  }

  private static Class<?> typeToClass(ClassLoader classLoader, Type type) throws ClassNotFoundException {
    switch (type.getSort()) {
      case Type.VOID:
        return void.class;
      case Type.BOOLEAN:
        return boolean.class;
      case Type.CHAR:
        return char.class;
      case Type.BYTE:
        return byte.class;
      case Type.SHORT:
        return short.class;
      case Type.INT:
        return int.class;
      case Type.FLOAT:
        return float.class;
      case Type.LONG:
        return long.class;
      case Type.DOUBLE:
        return double.class;
      case Type.ARRAY: {
        Class<?> currentClass = typeToClass(classLoader, type.getElementType());
        for (int i = 0; i < type.getDimensions(); i++) {
          currentClass = Array.newInstance(currentClass, 0).getClass();
        }
        return currentClass;
      }
      case Type.OBJECT:
        return Class.forName(type.getClassName(), false, classLoader);
      default:
        throw new IllegalArgumentException("Unsupported class type: " + type);
    }
  }

  private static class DelegateJarFile extends JarFile {
    private final JarFile jarFile;

    public DelegateJarFile(File file, JarFile jarFile) throws IOException {
      super(file);
      this.jarFile = jarFile;
    }

    @Override
    public Manifest getManifest() throws IOException {
      return jarFile.getManifest();
    }

    @Override
    public JarEntry getJarEntry(String name) {
      return jarFile.getJarEntry(name);
    }

    @Override
    public ZipEntry getEntry(String name) {
      return jarFile.getEntry(name);
    }

    @Override
    public Enumeration<JarEntry> entries() {
      return jarFile.entries();
    }

    @Override
    public Stream<JarEntry> stream() {
      return jarFile.stream();
    }

    @Override
    public InputStream getInputStream(ZipEntry ze) throws IOException {
      if (ze.getName().startsWith(CSN_CLASS_FILE_PREFIX)
          && !ze.getName().startsWith(CSN_LOADER_CLASS_FILE_PREFIX)
          && !ze.getName().startsWith(CSN_INTERNAL_CLASS_FILE_PREFIX)
          && ze.getName().endsWith(".class")) {
        byte[] bytes;
        try (InputStream in = jarFile.getInputStream(ze)) {
          bytes = readAllBytes(in);
        }
        bytes = transformBaseClass(bytes);
        if (ENABLE_DEBUG) {
          Path dumpPath = Paths.get("callsite-nbt", ze.getName());
          Files.createDirectories(dumpPath.getParent());
          Files.write(dumpPath, bytes);
        }
        return new ByteArrayInputStream(bytes);
      } else {
        return jarFile.getInputStream(ze);
      }
    }

    @Override
    public String getComment() {
      return jarFile.getComment();
    }

    @Override
    public String getName() {
      return jarFile.getName();
    }

    @Override
    public int size() {
      return jarFile.size();
    }

    @Override
    public void close() throws IOException {
      super.close();
      jarFile.close();
    }
  }

  private static class RefOnlyClassLoader extends ClassLoader {
    private final JarFile jar;

    public RefOnlyClassLoader(ClassLoader parent, JarFile jar) {
      super(parent);
      this.jar = jar;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
      synchronized (getClassLoadingLock(name)) {
        Class<?> c = findLoadedClass(name);
        if (c == null) {
          c = name.startsWith(CSN_REF_PACKAGE) ? findClass(name) : getParent().loadClass(name);
        }
        if (resolve) {
          resolveClass(c);
        }
        return c;
      }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
      if (name.startsWith(CSN_REF_PACKAGE)) {
        String path = name.replace('.', '/').concat(".class");
        JarEntry entry = jar.getJarEntry(path);
        if (entry != null) {
          try (InputStream in = jar.getInputStream(entry)) {
            byte[] bytes = readAllBytes(in);
            return defineClass(name, bytes, 0, bytes.length, null);
          } catch (IOException e) {
            throw new ClassNotFoundException(name, e);
          }
        }
      }
      throw new ClassNotFoundException(name);
    }
  }

  private static class TransformRemapper extends Remapper {
    private final RefOnlyClassLoader refOnlyClassLoader;

    public TransformRemapper(RefOnlyClassLoader refOnlyClassLoader) {
      this.refOnlyClassLoader = refOnlyClassLoader;
    }

    private Class<?> fetchRefClass(String internalName) {
      try {
        return refOnlyClassLoader.loadClass(internalName.replace('/', '.'));
      } catch (ClassNotFoundException e) {
        throw new IllegalStateException("No ref class found: " + internalName);
      }
    }

    private Field fetchRefField(String owner, String name, String descriptor) {
      Class<?> clazz = fetchRefClass(owner);
      for (Field field : clazz.getFields()) {
        if (field.getName().equals(name) && Type.getDescriptor(field.getType()).equals(descriptor)) {
          return field;
        }
      }
      throw new IllegalStateException("No ref class field found: L" + owner + ";" + name + ":" + descriptor);
    }

    private Executable fetchRefMethod(String owner, String name, String descriptor) {
      Class<?> clazz = fetchRefClass(owner);
      boolean isInit = name.equals("<init>");
      for (Executable method : isInit ? clazz.getDeclaredConstructors() : clazz.getMethods()) {
        Type methodType = isInit ? Type.getType((Constructor<?>) method) : Type.getType((Method) method);
        if ((isInit || method.getName().equals(name)) && methodType.getDescriptor().equals(descriptor)) {
          return method;
        }
      }
      throw new IllegalStateException("No ref class method found: L" + owner + ";" + name + descriptor);
    }

    private HandleBy fetchHandleBy(AnnotatedElement element) {
      HandleBy.List list = element.getAnnotation(HandleBy.List.class);
      if (list != null) {
        return CbVersion.match(list.value());
      }
      HandleBy single = element.getAnnotation(HandleBy.class);
      return CbVersion.match(new HandleBy[]{single});
    }

    public HandleBy fetchClass(String internalName) {
      return fetchHandleBy(fetchRefClass(internalName));
    }

    public HandleBy fetchField(String owner, String name, String descriptor) {
      return fetchHandleBy(fetchRefField(owner, name, descriptor));
    }

    public HandleBy fetchMethod(String owner, String name, String descriptor) {
      return fetchHandleBy(fetchRefMethod(owner, name, descriptor));
    }

    @Override
    public String mapMethodName(String owner, String name, String descriptor) {
      if (!owner.startsWith(CSN_REF_CLASS_FILE_PREFIX)) {
        return name;
      }
      HandleBy handleBy = fetchMethod(owner, name, descriptor);
      if (handleBy == null || handleBy.reference().isEmpty()) {
        return name;
      }
      String[] reference = parseMethod(handleBy.reference());
      return reference[1];
    }

    @Override
    public String mapFieldName(String owner, String name, String descriptor) {
      if (!owner.startsWith(CSN_REF_CLASS_FILE_PREFIX)) {
        return name;
      }
      HandleBy handleBy = fetchField(owner, name, descriptor);
      if (handleBy == null || handleBy.reference().isEmpty()) {
        return name;
      }
      String[] reference = parseField(handleBy.reference());
      return reference[1];
    }

    @Override
    public String map(String internalName) {
      if (!internalName.startsWith(CSN_REF_CLASS_FILE_PREFIX)) {
        return internalName;
      }
      HandleBy handleBy = fetchClass(internalName);
      if (handleBy == null || handleBy.reference().isEmpty()) {
        return internalName;
      }
      return parseClass(handleBy.reference());
    }
  }

  private static class TransformClassVisitor extends ClassRemapper {
    private final TransformRemapper remapper;

    public TransformClassVisitor(ClassVisitor classVisitor, TransformRemapper remapper) {
      super(Opcodes.ASM9, classVisitor, remapper);
      this.remapper = remapper;
    }

    @Override
    protected MethodVisitor createMethodRemapper(MethodVisitor methodVisitor) {
      return new TransformMethodRemapper(methodVisitor, remapper);
    }
  }

  private static class TransformMethodRemapper extends MethodRemapper {
    private final TransformRemapper remapper;

    public TransformMethodRemapper(MethodVisitor methodVisitor, TransformRemapper remapper) {
      super(Opcodes.ASM9, methodVisitor, remapper);
      this.remapper = remapper;
    }

    private void visitThrow(String message) {
      try {
        super.visitLdcInsn(message);
        super.visitMethodInsn(Opcodes.INVOKESTATIC,
            Type.getInternalName(Spy.class),
            Spy.class.getDeclaredMethod("throwException", String.class).getName(),
            "(Ljava/lang/String;)Ljava/lang/Object;",
            false
        );
        super.visitInsn(Opcodes.POP);
      } catch (NoSuchMethodException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
      if (!owner.startsWith(CSN_REF_CLASS_FILE_PREFIX)) {
        super.visitFieldInsn(opcode, owner, name, descriptor);
        return;
      }
      HandleBy handleBy = remapper.fetchField(owner, name, descriptor);
      if (handleBy == null || handleBy.reference().isEmpty()) {
        visitThrow("field L" + owner + ";" + name + ":" + descriptor + " not available in this minecraft version");
        super.visitFieldInsn(opcode, owner, name, descriptor);
        return;
      }
      String[] reference = parseField(handleBy.reference());
      if (handleBy.accessor()) {
        boolean isStatic = (opcode == Opcodes.GETSTATIC || opcode == Opcodes.PUTSTATIC);
        boolean isSet = (opcode == Opcodes.PUTSTATIC || opcode == Opcodes.PUTFIELD);
        super.visitInvokeDynamicInsn(
            reference[1],
            "(" + (isStatic ? "" : ("L" + owner + ";")) + (isSet ? descriptor : "") + ")" + (isSet ? "V" : descriptor),
            new Handle(
                Opcodes.H_INVOKESTATIC,
                Type.getInternalName(Spy.class),
                "bootstrap",
                "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;ILjava/lang/String;Ljava/lang/String;)Ljava/lang/invoke/CallSite;",
                false),
            opcode,
            reference[0],
            reference[2]
        );
      } else {
        super.visitFieldInsn(opcode, reference[0], reference[1], reference[2]);
      }
    }

    @Override
    public void visitMethodInsn(int opcodeAndSource, String owner, String name, String descriptor, boolean isInterface) {
      if (!owner.startsWith(CSN_REF_CLASS_FILE_PREFIX)) {
        super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface);
        return;
      }
      int source = opcodeAndSource & Opcodes.SOURCE_MASK;
      int opcode = opcodeAndSource & ~Opcodes.SOURCE_MASK;

      HandleBy handleBy = remapper.fetchMethod(owner, name, descriptor);
      if (handleBy == null || handleBy.reference().isEmpty()) {
        visitThrow("method L" + owner + ";" + name + descriptor + " not available in this minecraft version");
        super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface);
        return;
      }
      if (handleBy.isInterface() && opcode != Opcodes.INVOKESTATIC) {
        opcode = Opcodes.INVOKEINTERFACE;
      }
      String[] reference = parseMethod(handleBy.reference());
      if (handleBy.accessor()) {
        super.visitInvokeDynamicInsn(
            reference[1],
            (opcode == Opcodes.INVOKESTATIC) ? descriptor : ("(L" + owner + ";" + descriptor.substring(1)),
            new Handle(
                Opcodes.H_INVOKESTATIC,
                Type.getInternalName(Spy.class),
                "bootstrap",
                "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;ILjava/lang/String;Ljava/lang/String;)Ljava/lang/invoke/CallSite;",
                false),
            opcode,
            reference[0],
            reference[2]
        );
      } else {
        super.visitMethodInsn(opcode | source, reference[0], reference[1], reference[2], handleBy.isInterface());
      }
    }
  }

  public static class Spy {
    private Spy() {
      throw new UnsupportedOperationException();
    }

    public static CallSite bootstrap(
        MethodHandles.Lookup callerLookup,
        String name,
        MethodType type,
        int opcode,
        String owner,
        String describe
    ) throws Exception {
      try {
        Class<?> caller = callerLookup.lookupClass();
        ClassLoader classLoader = caller.getClassLoader();
        Class<?> ownerClass = Class.forName(owner.replace('/', '.'), false, classLoader);
        if (opcode == Opcodes.INVOKEVIRTUAL
            || opcode == Opcodes.INVOKESPECIAL
            || opcode == Opcodes.INVOKESTATIC
            || opcode == Opcodes.INVOKEINTERFACE) {
          return bootstrapMethod(caller, type, classLoader, ownerClass, name, describe, opcode);
        } else if (opcode == Opcodes.GETSTATIC
            || opcode == Opcodes.PUTSTATIC
            || opcode == Opcodes.GETFIELD
            || opcode == Opcodes.PUTFIELD) {
          return bootstrapField(caller, type, classLoader, ownerClass, name, describe, opcode);
        } else {
          throw new IllegalStateException("Unsupported bootstrap opcode: " + opcode);
        }
      } catch (Exception e) {
        logger.log(Level.SEVERE, "failed to bootstrap method(owner=" + owner + ",name=" + name + ",type=" + type + ")", e);
        throw e;
      }
    }

    private static CallSite bootstrapMethod(
        Class<?> caller,
        MethodType type,
        ClassLoader classLoader,
        Class<?> ownerClass,
        String name,
        String describe,
        int opcode
    ) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException {
      Type targetType = Type.getMethodType(describe);
      Type[] targetArgumentTypes = targetType.getArgumentTypes();
      Class<?>[] targetArgumentClasses = new Class[targetArgumentTypes.length];
      for (int i = 0; i < targetArgumentTypes.length; i++) {
        targetArgumentClasses[i] = typeToClass(classLoader, targetArgumentTypes[i]);
      }
      MethodType targetMethodType = MethodType.methodType(
          typeToClass(classLoader, targetType.getReturnType()),
          targetArgumentClasses
      );
      MethodHandle handle;
      if (opcode == Opcodes.INVOKESTATIC) {
        handle = lookup.findStatic(ownerClass, name, targetMethodType).asType(type);
      } else if (opcode == Opcodes.INVOKESPECIAL) {
        handle = lookup.findSpecial(ownerClass, name, targetMethodType, caller).asType(type);
      } else if (opcode == Opcodes.INVOKEVIRTUAL || opcode == Opcodes.INVOKEINTERFACE) {
        handle = lookup.findVirtual(ownerClass, name, targetMethodType).asType(type);
      } else {
        throw new IllegalStateException("Unsupported invokeMethod opcode: " + opcode);
      }
      return new ConstantCallSite(handle);
    }

    private static CallSite bootstrapField(
        Class<?> caller,
        MethodType type,
        ClassLoader classLoader,
        Class<?> ownerClass,
        String name,
        String describe,
        int opcode
    ) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
      Class<?> targetClass = typeToClass(classLoader, Type.getType(describe));
      MethodHandle handle;
      if (opcode == Opcodes.GETSTATIC) {
        handle = lookup.findStaticGetter(ownerClass, name, targetClass).asType(type);
      } else if (opcode == Opcodes.PUTSTATIC) {
        handle = lookup.findStaticSetter(ownerClass, name, targetClass).asType(type);
      } else if (opcode == Opcodes.GETFIELD) {
        handle = lookup.findGetter(ownerClass, name, targetClass).asType(type);
      } else if (opcode == Opcodes.PUTFIELD) {
        handle = lookup.findSetter(ownerClass, name, targetClass).asType(type);
      } else {
        throw new IllegalStateException("Unsupported invokeField opcode: " + opcode);
      }
      return new ConstantCallSite(handle);
    }

    public static <T> T throwException(String message) {
      throw new IllegalStateException(message);
    }

    public static <T, E extends Throwable> T throwException(Throwable e) throws E {
      throw (E) e;
    }

    public static void info(String message) {
      logger.info(message);
    }
  }
}
