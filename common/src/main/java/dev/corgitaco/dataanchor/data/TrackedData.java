/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.data;

import com.google.gson.annotations.SerializedName;
import net.minecraft.nbt.*;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public interface TrackedData<T> extends Supplier<T> {

    @Nullable
    default CompoundTag save() {
        try {
            return (CompoundTag) toTag(this);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    default void load(CompoundTag tag) {
        try {
            updateExistingFromTag(tag, this);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    static Tag toTag(Object obj) throws IllegalAccessException {
        if (obj instanceof Integer) {
            return IntTag.valueOf((Integer) obj);
        }
        if (obj instanceof Boolean) {
            return ByteTag.valueOf((Boolean) obj ? (byte) 1 : (byte) 0);
        }
        if (obj instanceof Long) {
            return LongTag.valueOf((Long) obj);
        }
        if (obj instanceof Float) {
            return FloatTag.valueOf((Float) obj);
        }
        if (obj instanceof Double) {
            return DoubleTag.valueOf((Double) obj);
        }
        if (obj instanceof Byte) {
            return ByteTag.valueOf((Byte) obj);
        }
        if (obj instanceof Short) {
            return ShortTag.valueOf((Short) obj);
        }
        if (obj instanceof String) {
            return StringTag.valueOf((String) obj);
        }
        if (obj instanceof int[]) {
            return new IntArrayTag((int[]) obj);
        }
        if (obj instanceof long[]) {
            return new LongArrayTag((long[]) obj);
        }
        if (obj instanceof byte[]) {
            return new ByteArrayTag((byte[]) obj);
        }
        if (obj instanceof float[] floats) {
            ListTag list = new ListTag();
            for (float aFloat : floats) {
                list.add(FloatTag.valueOf(aFloat));
            }
            return list;
        }
        if (obj instanceof double[]) {
            ListTag list = new ListTag();
            for (double aDouble : (double[]) obj) {
                list.add(DoubleTag.valueOf(aDouble));
            }
            return list;
        }
        if (obj instanceof boolean[]) {
            ListTag list = new ListTag();
            for (boolean b : (boolean[]) obj) {
                list.add(ByteTag.valueOf(b ? (byte) 1 : (byte) 0));
            }
            return list;
        }
        if (obj instanceof short[]) {
            ListTag list = new ListTag();
            for (short s : (short[]) obj) {
                list.add(ShortTag.valueOf(s));
            }
            return list;
        }
        if (obj instanceof char[]) {
            return StringTag.valueOf(new String((char[]) obj));
        }
        if (obj instanceof Collection) {
            ListTag listTag = new ListTag();
            for (Object o : (Collection<?>) obj) {
                listTag.add(toTag(o));
            }
            return listTag;
        }
        if (obj instanceof Map) {
            CompoundTag compoundTag = new CompoundTag();
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) obj).entrySet()) {
                compoundTag.put(entry.getKey().toString(), toTag(entry.getValue()));
            }
            return compoundTag;
        }
        if (obj instanceof Enum) {
            return StringTag.valueOf(((Enum<?>) obj).name());
        }

        CompoundTag tag = new CompoundTag();
        for (Field declaredField : obj.getClass().getDeclaredFields()) {
            int modifiers = declaredField.getModifiers();
            if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers)) {
                continue;
            }

            declaredField.setAccessible(true);
            Object obj1 = declaredField.get(obj);
            if (obj1 == null) {
                continue;
            }
            tag.put(declaredField.getName(), toTag(obj1));
        }

        return tag;
    }

    static <T> T updateExistingFromTag(Tag tag, T obj) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        if (tag instanceof CompoundTag compoundTag) {
            for (Field declaredField : obj.getClass().getDeclaredFields()) {
                int modifiers = declaredField.getModifiers();
                if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers)) {
                    continue;
                }

                declaredField.setAccessible(true);
                String name = declaredField.getName();
                if (declaredField.isAnnotationPresent(SerializedName.class)) {
                    SerializedName annotation = declaredField.getAnnotation(SerializedName.class);
                    name = annotation.value();
                    if (!compoundTag.contains(name)) {
                        if (annotation.alternate().length > 0) {
                            for (String s : annotation.alternate()) {
                                if (compoundTag.contains(s)) {
                                    name = s;
                                    break;
                                }
                            }
                        }
                    }
                }


                Class<?> declaringClass = declaredField.getType();
                if (declaringClass.isPrimitive()) {
                    if (declaringClass == boolean.class || declaringClass == Boolean.class) {
                        declaredField.set(obj, compoundTag.getBoolean(name).get().booleanValue());
                    }
                    if (declaringClass == int.class || declaringClass == Integer.class) {
                        declaredField.set(obj, compoundTag.getInt(name).get().intValue());
                    }
                    if (declaringClass == long.class || declaringClass == Long.class) {
                        declaredField.set(obj, compoundTag.getLong(name).get().longValue());
                    }
                    if (declaringClass == float.class || declaringClass == Float.class) {
                        declaredField.set(obj, compoundTag.getFloat(name).get().floatValue());
                    }
                    if (declaringClass == double.class || declaringClass == Double.class) {
                        declaredField.set(obj, compoundTag.getDouble(name).get().doubleValue());
                    }
                    if (declaringClass == byte.class || declaringClass == Byte.class) {
                        declaredField.set(obj, compoundTag.getByte(name).get().byteValue());
                    }
                    if (declaringClass == short.class || declaringClass == Short.class) {
                        declaredField.set(obj, compoundTag.getShort(name).get().shortValue());
                    }
                    if (declaringClass == char.class || declaringClass == Character.class) {
                        declaredField.set(obj, (char) compoundTag.getInt(name).get().intValue());
                    }
                }

                if (declaringClass == String.class) {
                    declaredField.set(obj, compoundTag.getString(name));
                }
                if (declaringClass.isInstance(Collection.class)) {
                    Object o = declaredField.get(obj);

                    if (o instanceof Collection<?> collection) {
                        collection.clear();
                        ListTag list = compoundTag.getListOrEmpty(name);
                        for (Tag tag1 : list) {
                            collection.add(fromTag(tag1, null));
                        }
                    }
                    declaredField.set(obj, compoundTag.getListOrEmpty(name));
                }

                if (declaredField.getType().isEnum()) {
                    declaredField.set(obj, Enum.valueOf((Class<Enum>) declaredField.getType(), compoundTag.getString(name).get()));
                }

                if (declaredField.getType().isArray()) {
                    if (declaringClass == int[].class) {
                        declaredField.set(obj, compoundTag.getIntArray(name));
                    }
                    if (declaringClass == long[].class) {
                        declaredField.set(obj, compoundTag.getLongArray(name));
                    }
                    if (declaringClass == byte[].class) {
                        declaredField.set(obj, compoundTag.getByteArray(name));
                    }
                    if (declaringClass == float[].class) {
                        ListTag list = compoundTag.getListOrEmpty(name);
                        float[] result = new float[list.size()];
                        for (int i = 0; i < list.size(); i++) {
                            FloatTag tag1 = (FloatTag) list.get(i);
                            result[i] = tag1.floatValue();
                        }
                        declaredField.set(obj, result);
                    }
                    if (declaringClass == double[].class) {
                        ListTag list = compoundTag.getListOrEmpty(name);
                        double[] result = new double[list.size()];
                        for (int i = 0; i < list.size(); i++) {
                            DoubleTag tag1 = (DoubleTag) list.get(i);
                            result[i] = tag1.doubleValue();
                        }
                        declaredField.set(obj, result);
                    }
                    if (declaringClass == boolean[].class) {
                        ListTag list = compoundTag.getListOrEmpty(name);
                        boolean[] result = new boolean[list.size()];
                        for (int i = 0; i < list.size(); i++) {
                            ByteTag tag1 = (ByteTag) list.get(i);
                            result[i] = tag1.byteValue() == 1;
                        }
                        declaredField.set(obj, result);
                    }
                    if (declaringClass == short[].class) {
                        ListTag list = compoundTag.getListOrEmpty(name);
                        short[] result = new short[list.size()];
                        for (int i = 0; i < list.size(); i++) {
                            ShortTag tag1 = (ShortTag) list.get(i);
                            result[i] = tag1.shortValue();
                        }
                        declaredField.set(obj, result);
                    }
                    if (declaringClass == char[].class) {
                        ListTag list = compoundTag.getListOrEmpty(name);
                        char[] result = new char[list.size()];
                        for (int i = 0; i < list.size(); i++) {
                            IntTag tag1 = (IntTag) list.get(i);
                            result[i] = (char) tag1.intValue();
                        }
                        declaredField.set(obj, result);
                    }

                    if (declaringClass.isInstance(Map.class)) {
                        declaredField.set(obj, fromTag(compoundTag.get(name), declaredField.getType()));
                    }
                }
            }
        }
        return obj;
    }

    static <T> T fromTag(Tag tag, Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (tag instanceof IntTag intTag) {
            return (T) (Integer) intTag.intValue();
        }

        if (tag instanceof LongTag longTag) {
            return (T) (Long) longTag.longValue();
        }
        if (tag instanceof FloatTag floatTag) {
            return (T) (Float) floatTag.floatValue();
        }
        if (tag instanceof DoubleTag doubleTag) {
            return (T) (Double) doubleTag.doubleValue();
        }
        if (tag instanceof ByteTag byteTag) {
            return (T) (Byte) byteTag.byteValue();
        }
        if (tag instanceof ShortTag shortTag) {
            return (T) (Short) shortTag.shortValue();
        }
        if (tag instanceof StringTag stringTag) {
            if (clazz.isEnum()) {
                return (T) Enum.valueOf((Class<Enum>) clazz, stringTag.value());
            }
            return (T) stringTag.value();
        }

        if (tag instanceof IntArrayTag intArrayTag) {
            return (T) intArrayTag.getAsIntArray();
        }

        if (tag instanceof LongArrayTag longArrayTag) {
            return (T) longArrayTag.getAsLongArray();
        }
        if (tag instanceof ByteArrayTag byteArrayTag) {
            return (T) byteArrayTag.getAsByteArray();
        }

        if (tag instanceof CompoundTag compoundTag) {
            Map<String, Tag> entries = new HashMap<>();

            for (String key : compoundTag.keySet()) {
                entries.put(key, compoundTag.get(key));
            }

            T t = clazz.getConstructor().newInstance();

            for (Field declaredField : clazz.getDeclaredFields()) {
                int modifiers = declaredField.getModifiers();
                if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers)) {
                    continue;
                }
                declaredField.setAccessible(true);
                String name = getName(declaredField, entries);

                Tag tag1 = entries.get(name);
                if (tag1 != null) {
                    declaredField.set(t, fromTag(tag1, declaredField.getType()));
                }
            }

            return t;
        }

        if (tag instanceof ListTag listTag) {
            T t = clazz.getConstructor().newInstance();

            if (t instanceof Collection collection) {
                for (Tag tag1 : listTag) {
                    collection.add(fromTag(tag1, clazz));
                }
            }


            return t;
        }

        return null;
    }

    private static String getName(Field declaredField, Map<String, Tag> entries) {
        String name = declaredField.getName();
        if (declaredField.isAnnotationPresent(SerializedName.class)) {
            SerializedName annotation = declaredField.getAnnotation(SerializedName.class);
            name = annotation.value();
            if (!entries.containsKey(name)) {
                if (annotation.alternate().length > 0) {
                    for (String s : annotation.alternate()) {
                        if (entries.containsKey(s)) {
                            name = s;
                            break;
                        }
                    }
                }
            }
        }
        return name;
    }
}