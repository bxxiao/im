// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ChatMsgProto.proto

package com.bx.im.proto;

public final class ChatMsgProto {
  private ChatMsgProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface ChatMsgOrBuilder extends
      // @@protoc_insertion_point(interface_extends:im.ChatMsg)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <pre>
     * 0-单聊消息；1-群聊消息
     * </pre>
     *
     * <code>int32 type = 1;</code>
     * @return The type.
     */
    int getType();

    /**
     * <pre>
     * uuid（客户端生成，对于群聊，可能暂时用不到该字段）
     * </pre>
     *
     * <code>string msgId = 2;</code>
     * @return The msgId.
     */
    java.lang.String getMsgId();
    /**
     * <pre>
     * uuid（客户端生成，对于群聊，可能暂时用不到该字段）
     * </pre>
     *
     * <code>string msgId = 2;</code>
     * @return The bytes for msgId.
     */
    com.google.protobuf.ByteString
        getMsgIdBytes();

    /**
     * <code>int64 msgSeq = 3;</code>
     * @return The msgSeq.
     */
    long getMsgSeq();

    /**
     * <code>int64 fromUid = 4;</code>
     * @return The fromUid.
     */
    long getFromUid();

    /**
     * <pre>
     * 表示用户或群id（对应单聊、群聊）
     * </pre>
     *
     * <code>int64 toId = 5;</code>
     * @return The toId.
     */
    long getToId();

    /**
     * <code>string content = 6;</code>
     * @return The content.
     */
    java.lang.String getContent();
    /**
     * <code>string content = 6;</code>
     * @return The bytes for content.
     */
    com.google.protobuf.ByteString
        getContentBytes();

    /**
     * <code>string time = 7;</code>
     * @return The time.
     */
    java.lang.String getTime();
    /**
     * <code>string time = 7;</code>
     * @return The bytes for time.
     */
    com.google.protobuf.ByteString
        getTimeBytes();
  }
  /**
   * <pre>
   *单聊、群聊消息
   * </pre>
   *
   * Protobuf type {@code im.ChatMsg}
   */
  public static final class ChatMsg extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:im.ChatMsg)
      ChatMsgOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use ChatMsg.newBuilder() to construct.
    private ChatMsg(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private ChatMsg() {
      msgId_ = "";
      content_ = "";
      time_ = "";
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new ChatMsg();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private ChatMsg(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 8: {

              type_ = input.readInt32();
              break;
            }
            case 18: {
              java.lang.String s = input.readStringRequireUtf8();

              msgId_ = s;
              break;
            }
            case 24: {

              msgSeq_ = input.readInt64();
              break;
            }
            case 32: {

              fromUid_ = input.readInt64();
              break;
            }
            case 40: {

              toId_ = input.readInt64();
              break;
            }
            case 50: {
              java.lang.String s = input.readStringRequireUtf8();

              content_ = s;
              break;
            }
            case 58: {
              java.lang.String s = input.readStringRequireUtf8();

              time_ = s;
              break;
            }
            default: {
              if (!parseUnknownField(
                  input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.bx.im.proto.ChatMsgProto.internal_static_im_ChatMsg_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.bx.im.proto.ChatMsgProto.internal_static_im_ChatMsg_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.bx.im.proto.ChatMsgProto.ChatMsg.class, com.bx.im.proto.ChatMsgProto.ChatMsg.Builder.class);
    }

    public static final int TYPE_FIELD_NUMBER = 1;
    private int type_;
    /**
     * <pre>
     * 0-单聊消息；1-群聊消息
     * </pre>
     *
     * <code>int32 type = 1;</code>
     * @return The type.
     */
    @java.lang.Override
    public int getType() {
      return type_;
    }

    public static final int MSGID_FIELD_NUMBER = 2;
    private volatile java.lang.Object msgId_;
    /**
     * <pre>
     * uuid（客户端生成，对于群聊，可能暂时用不到该字段）
     * </pre>
     *
     * <code>string msgId = 2;</code>
     * @return The msgId.
     */
    @java.lang.Override
    public java.lang.String getMsgId() {
      java.lang.Object ref = msgId_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        msgId_ = s;
        return s;
      }
    }
    /**
     * <pre>
     * uuid（客户端生成，对于群聊，可能暂时用不到该字段）
     * </pre>
     *
     * <code>string msgId = 2;</code>
     * @return The bytes for msgId.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getMsgIdBytes() {
      java.lang.Object ref = msgId_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        msgId_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int MSGSEQ_FIELD_NUMBER = 3;
    private long msgSeq_;
    /**
     * <code>int64 msgSeq = 3;</code>
     * @return The msgSeq.
     */
    @java.lang.Override
    public long getMsgSeq() {
      return msgSeq_;
    }

    public static final int FROMUID_FIELD_NUMBER = 4;
    private long fromUid_;
    /**
     * <code>int64 fromUid = 4;</code>
     * @return The fromUid.
     */
    @java.lang.Override
    public long getFromUid() {
      return fromUid_;
    }

    public static final int TOID_FIELD_NUMBER = 5;
    private long toId_;
    /**
     * <pre>
     * 表示用户或群id（对应单聊、群聊）
     * </pre>
     *
     * <code>int64 toId = 5;</code>
     * @return The toId.
     */
    @java.lang.Override
    public long getToId() {
      return toId_;
    }

    public static final int CONTENT_FIELD_NUMBER = 6;
    private volatile java.lang.Object content_;
    /**
     * <code>string content = 6;</code>
     * @return The content.
     */
    @java.lang.Override
    public java.lang.String getContent() {
      java.lang.Object ref = content_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        content_ = s;
        return s;
      }
    }
    /**
     * <code>string content = 6;</code>
     * @return The bytes for content.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getContentBytes() {
      java.lang.Object ref = content_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        content_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int TIME_FIELD_NUMBER = 7;
    private volatile java.lang.Object time_;
    /**
     * <code>string time = 7;</code>
     * @return The time.
     */
    @java.lang.Override
    public java.lang.String getTime() {
      java.lang.Object ref = time_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        time_ = s;
        return s;
      }
    }
    /**
     * <code>string time = 7;</code>
     * @return The bytes for time.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getTimeBytes() {
      java.lang.Object ref = time_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        time_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    private byte memoizedIsInitialized = -1;
    @java.lang.Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    @java.lang.Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (type_ != 0) {
        output.writeInt32(1, type_);
      }
      if (!getMsgIdBytes().isEmpty()) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 2, msgId_);
      }
      if (msgSeq_ != 0L) {
        output.writeInt64(3, msgSeq_);
      }
      if (fromUid_ != 0L) {
        output.writeInt64(4, fromUid_);
      }
      if (toId_ != 0L) {
        output.writeInt64(5, toId_);
      }
      if (!getContentBytes().isEmpty()) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 6, content_);
      }
      if (!getTimeBytes().isEmpty()) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 7, time_);
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (type_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(1, type_);
      }
      if (!getMsgIdBytes().isEmpty()) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, msgId_);
      }
      if (msgSeq_ != 0L) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(3, msgSeq_);
      }
      if (fromUid_ != 0L) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(4, fromUid_);
      }
      if (toId_ != 0L) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(5, toId_);
      }
      if (!getContentBytes().isEmpty()) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(6, content_);
      }
      if (!getTimeBytes().isEmpty()) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(7, time_);
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof com.bx.im.proto.ChatMsgProto.ChatMsg)) {
        return super.equals(obj);
      }
      com.bx.im.proto.ChatMsgProto.ChatMsg other = (com.bx.im.proto.ChatMsgProto.ChatMsg) obj;

      if (getType()
          != other.getType()) return false;
      if (!getMsgId()
          .equals(other.getMsgId())) return false;
      if (getMsgSeq()
          != other.getMsgSeq()) return false;
      if (getFromUid()
          != other.getFromUid()) return false;
      if (getToId()
          != other.getToId()) return false;
      if (!getContent()
          .equals(other.getContent())) return false;
      if (!getTime()
          .equals(other.getTime())) return false;
      if (!unknownFields.equals(other.unknownFields)) return false;
      return true;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      hash = (37 * hash) + TYPE_FIELD_NUMBER;
      hash = (53 * hash) + getType();
      hash = (37 * hash) + MSGID_FIELD_NUMBER;
      hash = (53 * hash) + getMsgId().hashCode();
      hash = (37 * hash) + MSGSEQ_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getMsgSeq());
      hash = (37 * hash) + FROMUID_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getFromUid());
      hash = (37 * hash) + TOID_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getToId());
      hash = (37 * hash) + CONTENT_FIELD_NUMBER;
      hash = (53 * hash) + getContent().hashCode();
      hash = (37 * hash) + TIME_FIELD_NUMBER;
      hash = (53 * hash) + getTime().hashCode();
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static com.bx.im.proto.ChatMsgProto.ChatMsg parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.bx.im.proto.ChatMsgProto.ChatMsg parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.bx.im.proto.ChatMsgProto.ChatMsg parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.bx.im.proto.ChatMsgProto.ChatMsg parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.bx.im.proto.ChatMsgProto.ChatMsg parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.bx.im.proto.ChatMsgProto.ChatMsg parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.bx.im.proto.ChatMsgProto.ChatMsg parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static com.bx.im.proto.ChatMsgProto.ChatMsg parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.bx.im.proto.ChatMsgProto.ChatMsg parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static com.bx.im.proto.ChatMsgProto.ChatMsg parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.bx.im.proto.ChatMsgProto.ChatMsg parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static com.bx.im.proto.ChatMsgProto.ChatMsg parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @java.lang.Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(com.bx.im.proto.ChatMsgProto.ChatMsg prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @java.lang.Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * <pre>
     *单聊、群聊消息
     * </pre>
     *
     * Protobuf type {@code im.ChatMsg}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:im.ChatMsg)
        com.bx.im.proto.ChatMsgProto.ChatMsgOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.bx.im.proto.ChatMsgProto.internal_static_im_ChatMsg_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.bx.im.proto.ChatMsgProto.internal_static_im_ChatMsg_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                com.bx.im.proto.ChatMsgProto.ChatMsg.class, com.bx.im.proto.ChatMsgProto.ChatMsg.Builder.class);
      }

      // Construct using com.bx.im.proto.ChatMsgProto.ChatMsg.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        type_ = 0;

        msgId_ = "";

        msgSeq_ = 0L;

        fromUid_ = 0L;

        toId_ = 0L;

        content_ = "";

        time_ = "";

        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.bx.im.proto.ChatMsgProto.internal_static_im_ChatMsg_descriptor;
      }

      @java.lang.Override
      public com.bx.im.proto.ChatMsgProto.ChatMsg getDefaultInstanceForType() {
        return com.bx.im.proto.ChatMsgProto.ChatMsg.getDefaultInstance();
      }

      @java.lang.Override
      public com.bx.im.proto.ChatMsgProto.ChatMsg build() {
        com.bx.im.proto.ChatMsgProto.ChatMsg result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public com.bx.im.proto.ChatMsgProto.ChatMsg buildPartial() {
        com.bx.im.proto.ChatMsgProto.ChatMsg result = new com.bx.im.proto.ChatMsgProto.ChatMsg(this);
        result.type_ = type_;
        result.msgId_ = msgId_;
        result.msgSeq_ = msgSeq_;
        result.fromUid_ = fromUid_;
        result.toId_ = toId_;
        result.content_ = content_;
        result.time_ = time_;
        onBuilt();
        return result;
      }

      @java.lang.Override
      public Builder clone() {
        return super.clone();
      }
      @java.lang.Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.setField(field, value);
      }
      @java.lang.Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }
      @java.lang.Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }
      @java.lang.Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, java.lang.Object value) {
        return super.setRepeatedField(field, index, value);
      }
      @java.lang.Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.addRepeatedField(field, value);
      }
      @java.lang.Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.bx.im.proto.ChatMsgProto.ChatMsg) {
          return mergeFrom((com.bx.im.proto.ChatMsgProto.ChatMsg)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(com.bx.im.proto.ChatMsgProto.ChatMsg other) {
        if (other == com.bx.im.proto.ChatMsgProto.ChatMsg.getDefaultInstance()) return this;
        if (other.getType() != 0) {
          setType(other.getType());
        }
        if (!other.getMsgId().isEmpty()) {
          msgId_ = other.msgId_;
          onChanged();
        }
        if (other.getMsgSeq() != 0L) {
          setMsgSeq(other.getMsgSeq());
        }
        if (other.getFromUid() != 0L) {
          setFromUid(other.getFromUid());
        }
        if (other.getToId() != 0L) {
          setToId(other.getToId());
        }
        if (!other.getContent().isEmpty()) {
          content_ = other.content_;
          onChanged();
        }
        if (!other.getTime().isEmpty()) {
          time_ = other.time_;
          onChanged();
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      @java.lang.Override
      public final boolean isInitialized() {
        return true;
      }

      @java.lang.Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.bx.im.proto.ChatMsgProto.ChatMsg parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (com.bx.im.proto.ChatMsgProto.ChatMsg) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private int type_ ;
      /**
       * <pre>
       * 0-单聊消息；1-群聊消息
       * </pre>
       *
       * <code>int32 type = 1;</code>
       * @return The type.
       */
      @java.lang.Override
      public int getType() {
        return type_;
      }
      /**
       * <pre>
       * 0-单聊消息；1-群聊消息
       * </pre>
       *
       * <code>int32 type = 1;</code>
       * @param value The type to set.
       * @return This builder for chaining.
       */
      public Builder setType(int value) {
        
        type_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       * 0-单聊消息；1-群聊消息
       * </pre>
       *
       * <code>int32 type = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearType() {
        
        type_ = 0;
        onChanged();
        return this;
      }

      private java.lang.Object msgId_ = "";
      /**
       * <pre>
       * uuid（客户端生成，对于群聊，可能暂时用不到该字段）
       * </pre>
       *
       * <code>string msgId = 2;</code>
       * @return The msgId.
       */
      public java.lang.String getMsgId() {
        java.lang.Object ref = msgId_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          msgId_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <pre>
       * uuid（客户端生成，对于群聊，可能暂时用不到该字段）
       * </pre>
       *
       * <code>string msgId = 2;</code>
       * @return The bytes for msgId.
       */
      public com.google.protobuf.ByteString
          getMsgIdBytes() {
        java.lang.Object ref = msgId_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          msgId_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <pre>
       * uuid（客户端生成，对于群聊，可能暂时用不到该字段）
       * </pre>
       *
       * <code>string msgId = 2;</code>
       * @param value The msgId to set.
       * @return This builder for chaining.
       */
      public Builder setMsgId(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        msgId_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       * uuid（客户端生成，对于群聊，可能暂时用不到该字段）
       * </pre>
       *
       * <code>string msgId = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearMsgId() {
        
        msgId_ = getDefaultInstance().getMsgId();
        onChanged();
        return this;
      }
      /**
       * <pre>
       * uuid（客户端生成，对于群聊，可能暂时用不到该字段）
       * </pre>
       *
       * <code>string msgId = 2;</code>
       * @param value The bytes for msgId to set.
       * @return This builder for chaining.
       */
      public Builder setMsgIdBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        
        msgId_ = value;
        onChanged();
        return this;
      }

      private long msgSeq_ ;
      /**
       * <code>int64 msgSeq = 3;</code>
       * @return The msgSeq.
       */
      @java.lang.Override
      public long getMsgSeq() {
        return msgSeq_;
      }
      /**
       * <code>int64 msgSeq = 3;</code>
       * @param value The msgSeq to set.
       * @return This builder for chaining.
       */
      public Builder setMsgSeq(long value) {
        
        msgSeq_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int64 msgSeq = 3;</code>
       * @return This builder for chaining.
       */
      public Builder clearMsgSeq() {
        
        msgSeq_ = 0L;
        onChanged();
        return this;
      }

      private long fromUid_ ;
      /**
       * <code>int64 fromUid = 4;</code>
       * @return The fromUid.
       */
      @java.lang.Override
      public long getFromUid() {
        return fromUid_;
      }
      /**
       * <code>int64 fromUid = 4;</code>
       * @param value The fromUid to set.
       * @return This builder for chaining.
       */
      public Builder setFromUid(long value) {
        
        fromUid_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int64 fromUid = 4;</code>
       * @return This builder for chaining.
       */
      public Builder clearFromUid() {
        
        fromUid_ = 0L;
        onChanged();
        return this;
      }

      private long toId_ ;
      /**
       * <pre>
       * 表示用户或群id（对应单聊、群聊）
       * </pre>
       *
       * <code>int64 toId = 5;</code>
       * @return The toId.
       */
      @java.lang.Override
      public long getToId() {
        return toId_;
      }
      /**
       * <pre>
       * 表示用户或群id（对应单聊、群聊）
       * </pre>
       *
       * <code>int64 toId = 5;</code>
       * @param value The toId to set.
       * @return This builder for chaining.
       */
      public Builder setToId(long value) {
        
        toId_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       * 表示用户或群id（对应单聊、群聊）
       * </pre>
       *
       * <code>int64 toId = 5;</code>
       * @return This builder for chaining.
       */
      public Builder clearToId() {
        
        toId_ = 0L;
        onChanged();
        return this;
      }

      private java.lang.Object content_ = "";
      /**
       * <code>string content = 6;</code>
       * @return The content.
       */
      public java.lang.String getContent() {
        java.lang.Object ref = content_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          content_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string content = 6;</code>
       * @return The bytes for content.
       */
      public com.google.protobuf.ByteString
          getContentBytes() {
        java.lang.Object ref = content_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          content_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string content = 6;</code>
       * @param value The content to set.
       * @return This builder for chaining.
       */
      public Builder setContent(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        content_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string content = 6;</code>
       * @return This builder for chaining.
       */
      public Builder clearContent() {
        
        content_ = getDefaultInstance().getContent();
        onChanged();
        return this;
      }
      /**
       * <code>string content = 6;</code>
       * @param value The bytes for content to set.
       * @return This builder for chaining.
       */
      public Builder setContentBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        
        content_ = value;
        onChanged();
        return this;
      }

      private java.lang.Object time_ = "";
      /**
       * <code>string time = 7;</code>
       * @return The time.
       */
      public java.lang.String getTime() {
        java.lang.Object ref = time_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          time_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string time = 7;</code>
       * @return The bytes for time.
       */
      public com.google.protobuf.ByteString
          getTimeBytes() {
        java.lang.Object ref = time_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          time_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string time = 7;</code>
       * @param value The time to set.
       * @return This builder for chaining.
       */
      public Builder setTime(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        time_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string time = 7;</code>
       * @return This builder for chaining.
       */
      public Builder clearTime() {
        
        time_ = getDefaultInstance().getTime();
        onChanged();
        return this;
      }
      /**
       * <code>string time = 7;</code>
       * @param value The bytes for time to set.
       * @return This builder for chaining.
       */
      public Builder setTimeBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        
        time_ = value;
        onChanged();
        return this;
      }
      @java.lang.Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @java.lang.Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:im.ChatMsg)
    }

    // @@protoc_insertion_point(class_scope:im.ChatMsg)
    private static final com.bx.im.proto.ChatMsgProto.ChatMsg DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new com.bx.im.proto.ChatMsgProto.ChatMsg();
    }

    public static com.bx.im.proto.ChatMsgProto.ChatMsg getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<ChatMsg>
        PARSER = new com.google.protobuf.AbstractParser<ChatMsg>() {
      @java.lang.Override
      public ChatMsg parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new ChatMsg(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<ChatMsg> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<ChatMsg> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public com.bx.im.proto.ChatMsgProto.ChatMsg getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_im_ChatMsg_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_im_ChatMsg_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\022ChatMsgProto.proto\022\002im\032\037google/protobu" +
      "f/timestamp.proto\"t\n\007ChatMsg\022\014\n\004type\030\001 \001" +
      "(\005\022\r\n\005msgId\030\002 \001(\t\022\016\n\006msgSeq\030\003 \001(\003\022\017\n\007fro" +
      "mUid\030\004 \001(\003\022\014\n\004toId\030\005 \001(\003\022\017\n\007content\030\006 \001(" +
      "\t\022\014\n\004time\030\007 \001(\tB\021\n\017com.bx.im.protob\006prot" +
      "o3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.google.protobuf.TimestampProto.getDescriptor(),
        });
    internal_static_im_ChatMsg_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_im_ChatMsg_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_im_ChatMsg_descriptor,
        new java.lang.String[] { "Type", "MsgId", "MsgSeq", "FromUid", "ToId", "Content", "Time", });
    com.google.protobuf.TimestampProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
