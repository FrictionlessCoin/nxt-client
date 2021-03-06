package nxt;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import nxt.crypto.Crypto;
import nxt.util.Convert;
import nxt.util.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public final class Transaction
  implements Comparable<Transaction>
{
  private static final byte TYPE_PAYMENT = 0;
  private static final byte TYPE_MESSAGING = 1;
  private static final byte TYPE_COLORED_COINS = 2;
  private static final byte SUBTYPE_PAYMENT_ORDINARY_PAYMENT = 0;
  private static final byte SUBTYPE_MESSAGING_ARBITRARY_MESSAGE = 0;
  private static final byte SUBTYPE_MESSAGING_ALIAS_ASSIGNMENT = 1;
  private static final byte SUBTYPE_MESSAGING_POLL_CREATION = 2;
  private static final byte SUBTYPE_MESSAGING_VOTE_CASTING = 3;
  private static final byte SUBTYPE_COLORED_COINS_ASSET_ISSUANCE = 0;
  private static final byte SUBTYPE_COLORED_COINS_ASSET_TRANSFER = 1;
  private static final byte SUBTYPE_COLORED_COINS_ASK_ORDER_PLACEMENT = 2;
  private static final byte SUBTYPE_COLORED_COINS_BID_ORDER_PLACEMENT = 3;
  private static final byte SUBTYPE_COLORED_COINS_ASK_ORDER_CANCELLATION = 4;
  private static final byte SUBTYPE_COLORED_COINS_BID_ORDER_CANCELLATION = 5;
  private final short deadline;
  private final byte[] senderPublicKey;
  private final Long recipientId;
  private final int amount;
  private final int fee;
  private final Long referencedTransactionId;
  private final Type type;
  
  public static Transaction newTransaction(int paramInt1, short paramShort, byte[] paramArrayOfByte, Long paramLong1, int paramInt2, int paramInt3, Long paramLong2)
    throws NxtException.ValidationException
  {
    return new Transaction(Transaction.Type.Payment.ORDINARY, paramInt1, paramShort, paramArrayOfByte, paramLong1, paramInt2, paramInt3, paramLong2, null);
  }
  
  public static Transaction newTransaction(int paramInt1, short paramShort, byte[] paramArrayOfByte, Long paramLong1, int paramInt2, int paramInt3, Long paramLong2, Attachment paramAttachment)
    throws NxtException.ValidationException
  {
    Transaction localTransaction = new Transaction(paramAttachment.getTransactionType(), paramInt1, paramShort, paramArrayOfByte, paramLong1, paramInt2, paramInt3, paramLong2, null);
    
    localTransaction.attachment = paramAttachment;
    return localTransaction;
  }
  
  static Transaction newTransaction(int paramInt1, short paramShort, byte[] paramArrayOfByte1, Long paramLong1, int paramInt2, int paramInt3, Long paramLong2, byte[] paramArrayOfByte2)
    throws NxtException.ValidationException
  {
    return new Transaction(Transaction.Type.Payment.ORDINARY, paramInt1, paramShort, paramArrayOfByte1, paramLong1, paramInt2, paramInt3, paramLong2, paramArrayOfByte2);
  }
  
  /* Error */
  static Transaction findTransaction(Long paramLong)
  {
    // Byte code:
    //   0: invokestatic 10	nxt/Db:getConnection	()Ljava/sql/Connection;
    //   3: astore_1
    //   4: aconst_null
    //   5: astore_2
    //   6: aload_1
    //   7: ldc 11
    //   9: invokeinterface 12 2 0
    //   14: astore_3
    //   15: aconst_null
    //   16: astore 4
    //   18: aload_3
    //   19: iconst_1
    //   20: aload_0
    //   21: invokevirtual 13	java/lang/Long:longValue	()J
    //   24: invokeinterface 14 4 0
    //   29: aload_3
    //   30: invokeinterface 15 1 0
    //   35: astore 5
    //   37: aconst_null
    //   38: astore 6
    //   40: aload 5
    //   42: invokeinterface 16 1 0
    //   47: ifeq +11 -> 58
    //   50: aload_1
    //   51: aload 5
    //   53: invokestatic 17	nxt/Transaction:getTransaction	(Ljava/sql/Connection;Ljava/sql/ResultSet;)Lnxt/Transaction;
    //   56: astore 6
    //   58: aload 5
    //   60: invokeinterface 18 1 0
    //   65: aload 6
    //   67: astore 7
    //   69: aload_3
    //   70: ifnull +35 -> 105
    //   73: aload 4
    //   75: ifnull +24 -> 99
    //   78: aload_3
    //   79: invokeinterface 19 1 0
    //   84: goto +21 -> 105
    //   87: astore 8
    //   89: aload 4
    //   91: aload 8
    //   93: invokevirtual 21	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   96: goto +9 -> 105
    //   99: aload_3
    //   100: invokeinterface 19 1 0
    //   105: aload_1
    //   106: ifnull +33 -> 139
    //   109: aload_2
    //   110: ifnull +23 -> 133
    //   113: aload_1
    //   114: invokeinterface 22 1 0
    //   119: goto +20 -> 139
    //   122: astore 8
    //   124: aload_2
    //   125: aload 8
    //   127: invokevirtual 21	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   130: goto +9 -> 139
    //   133: aload_1
    //   134: invokeinterface 22 1 0
    //   139: aload 7
    //   141: areturn
    //   142: astore 5
    //   144: aload 5
    //   146: astore 4
    //   148: aload 5
    //   150: athrow
    //   151: astore 9
    //   153: aload_3
    //   154: ifnull +35 -> 189
    //   157: aload 4
    //   159: ifnull +24 -> 183
    //   162: aload_3
    //   163: invokeinterface 19 1 0
    //   168: goto +21 -> 189
    //   171: astore 10
    //   173: aload 4
    //   175: aload 10
    //   177: invokevirtual 21	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   180: goto +9 -> 189
    //   183: aload_3
    //   184: invokeinterface 19 1 0
    //   189: aload 9
    //   191: athrow
    //   192: astore_3
    //   193: aload_3
    //   194: astore_2
    //   195: aload_3
    //   196: athrow
    //   197: astore 11
    //   199: aload_1
    //   200: ifnull +33 -> 233
    //   203: aload_2
    //   204: ifnull +23 -> 227
    //   207: aload_1
    //   208: invokeinterface 22 1 0
    //   213: goto +20 -> 233
    //   216: astore 12
    //   218: aload_2
    //   219: aload 12
    //   221: invokevirtual 21	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   224: goto +9 -> 233
    //   227: aload_1
    //   228: invokeinterface 22 1 0
    //   233: aload 11
    //   235: athrow
    //   236: astore_1
    //   237: new 24	java/lang/RuntimeException
    //   240: dup
    //   241: aload_1
    //   242: invokevirtual 25	java/sql/SQLException:getMessage	()Ljava/lang/String;
    //   245: aload_1
    //   246: invokespecial 26	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   249: athrow
    //   250: astore_1
    //   251: new 24	java/lang/RuntimeException
    //   254: dup
    //   255: new 28	java/lang/StringBuilder
    //   258: dup
    //   259: invokespecial 29	java/lang/StringBuilder:<init>	()V
    //   262: ldc 30
    //   264: invokevirtual 31	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   267: aload_0
    //   268: invokevirtual 32	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   271: ldc 33
    //   273: invokevirtual 31	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   276: invokevirtual 34	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   279: invokespecial 35	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   282: athrow
    // Line number table:
    //   Java source line #67	-> byte code offset #0
    //   Java source line #68	-> byte code offset #6
    //   Java source line #67	-> byte code offset #15
    //   Java source line #69	-> byte code offset #18
    //   Java source line #70	-> byte code offset #29
    //   Java source line #71	-> byte code offset #37
    //   Java source line #72	-> byte code offset #40
    //   Java source line #73	-> byte code offset #50
    //   Java source line #75	-> byte code offset #58
    //   Java source line #76	-> byte code offset #65
    //   Java source line #77	-> byte code offset #69
    //   Java source line #67	-> byte code offset #142
    //   Java source line #77	-> byte code offset #151
    //   Java source line #67	-> byte code offset #192
    //   Java source line #77	-> byte code offset #197
    //   Java source line #78	-> byte code offset #237
    //   Java source line #79	-> byte code offset #250
    //   Java source line #80	-> byte code offset #251
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	283	0	paramLong	Long
    //   3	225	1	localConnection	Connection
    //   236	10	1	localSQLException	SQLException
    //   250	1	1	localValidationException	NxtException.ValidationException
    //   5	214	2	localObject1	Object
    //   14	170	3	localPreparedStatement	PreparedStatement
    //   192	4	3	localThrowable1	Throwable
    //   16	158	4	localObject2	Object
    //   35	24	5	localResultSet	ResultSet
    //   142	7	5	localThrowable2	Throwable
    //   38	28	6	localTransaction1	Transaction
    //   87	5	8	localThrowable3	Throwable
    //   122	4	8	localThrowable4	Throwable
    //   151	39	9	localObject3	Object
    //   171	5	10	localThrowable5	Throwable
    //   197	37	11	localObject4	Object
    //   216	4	12	localThrowable6	Throwable
    // Exception table:
    //   from	to	target	type
    //   78	84	87	java/lang/Throwable
    //   113	119	122	java/lang/Throwable
    //   18	69	142	java/lang/Throwable
    //   18	69	151	finally
    //   142	153	151	finally
    //   162	168	171	java/lang/Throwable
    //   6	105	192	java/lang/Throwable
    //   142	192	192	java/lang/Throwable
    //   6	105	197	finally
    //   142	199	197	finally
    //   207	213	216	java/lang/Throwable
    //   0	139	236	java/sql/SQLException
    //   142	236	236	java/sql/SQLException
    //   0	139	250	nxt/NxtException$ValidationException
    //   142	236	250	nxt/NxtException$ValidationException
  }
  
  /* Error */
  static boolean hasTransaction(Long paramLong)
  {
    // Byte code:
    //   0: invokestatic 10	nxt/Db:getConnection	()Ljava/sql/Connection;
    //   3: astore_1
    //   4: aconst_null
    //   5: astore_2
    //   6: aload_1
    //   7: ldc 36
    //   9: invokeinterface 12 2 0
    //   14: astore_3
    //   15: aconst_null
    //   16: astore 4
    //   18: aload_3
    //   19: iconst_1
    //   20: aload_0
    //   21: invokevirtual 13	java/lang/Long:longValue	()J
    //   24: invokeinterface 14 4 0
    //   29: aload_3
    //   30: invokeinterface 15 1 0
    //   35: astore 5
    //   37: aload 5
    //   39: invokeinterface 16 1 0
    //   44: istore 6
    //   46: aload_3
    //   47: ifnull +35 -> 82
    //   50: aload 4
    //   52: ifnull +24 -> 76
    //   55: aload_3
    //   56: invokeinterface 19 1 0
    //   61: goto +21 -> 82
    //   64: astore 7
    //   66: aload 4
    //   68: aload 7
    //   70: invokevirtual 21	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   73: goto +9 -> 82
    //   76: aload_3
    //   77: invokeinterface 19 1 0
    //   82: aload_1
    //   83: ifnull +33 -> 116
    //   86: aload_2
    //   87: ifnull +23 -> 110
    //   90: aload_1
    //   91: invokeinterface 22 1 0
    //   96: goto +20 -> 116
    //   99: astore 7
    //   101: aload_2
    //   102: aload 7
    //   104: invokevirtual 21	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   107: goto +9 -> 116
    //   110: aload_1
    //   111: invokeinterface 22 1 0
    //   116: iload 6
    //   118: ireturn
    //   119: astore 5
    //   121: aload 5
    //   123: astore 4
    //   125: aload 5
    //   127: athrow
    //   128: astore 8
    //   130: aload_3
    //   131: ifnull +35 -> 166
    //   134: aload 4
    //   136: ifnull +24 -> 160
    //   139: aload_3
    //   140: invokeinterface 19 1 0
    //   145: goto +21 -> 166
    //   148: astore 9
    //   150: aload 4
    //   152: aload 9
    //   154: invokevirtual 21	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   157: goto +9 -> 166
    //   160: aload_3
    //   161: invokeinterface 19 1 0
    //   166: aload 8
    //   168: athrow
    //   169: astore_3
    //   170: aload_3
    //   171: astore_2
    //   172: aload_3
    //   173: athrow
    //   174: astore 10
    //   176: aload_1
    //   177: ifnull +33 -> 210
    //   180: aload_2
    //   181: ifnull +23 -> 204
    //   184: aload_1
    //   185: invokeinterface 22 1 0
    //   190: goto +20 -> 210
    //   193: astore 11
    //   195: aload_2
    //   196: aload 11
    //   198: invokevirtual 21	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   201: goto +9 -> 210
    //   204: aload_1
    //   205: invokeinterface 22 1 0
    //   210: aload 10
    //   212: athrow
    //   213: astore_1
    //   214: new 24	java/lang/RuntimeException
    //   217: dup
    //   218: aload_1
    //   219: invokevirtual 25	java/sql/SQLException:getMessage	()Ljava/lang/String;
    //   222: aload_1
    //   223: invokespecial 26	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   226: athrow
    // Line number table:
    //   Java source line #85	-> byte code offset #0
    //   Java source line #86	-> byte code offset #6
    //   Java source line #85	-> byte code offset #15
    //   Java source line #87	-> byte code offset #18
    //   Java source line #88	-> byte code offset #29
    //   Java source line #89	-> byte code offset #37
    //   Java source line #90	-> byte code offset #46
    //   Java source line #85	-> byte code offset #119
    //   Java source line #90	-> byte code offset #128
    //   Java source line #85	-> byte code offset #169
    //   Java source line #90	-> byte code offset #174
    //   Java source line #91	-> byte code offset #214
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	227	0	paramLong	Long
    //   3	202	1	localConnection	Connection
    //   213	10	1	localSQLException	SQLException
    //   5	191	2	localObject1	Object
    //   14	147	3	localPreparedStatement	PreparedStatement
    //   169	4	3	localThrowable1	Throwable
    //   16	135	4	localObject2	Object
    //   35	3	5	localResultSet	ResultSet
    //   119	7	5	localThrowable2	Throwable
    //   64	5	7	localThrowable3	Throwable
    //   99	4	7	localThrowable4	Throwable
    //   128	39	8	localObject3	Object
    //   148	5	9	localThrowable5	Throwable
    //   174	37	10	localObject4	Object
    //   193	4	11	localThrowable6	Throwable
    // Exception table:
    //   from	to	target	type
    //   55	61	64	java/lang/Throwable
    //   90	96	99	java/lang/Throwable
    //   18	46	119	java/lang/Throwable
    //   18	46	128	finally
    //   119	130	128	finally
    //   139	145	148	java/lang/Throwable
    //   6	82	169	java/lang/Throwable
    //   119	169	169	java/lang/Throwable
    //   6	82	174	finally
    //   119	176	174	finally
    //   184	190	193	java/lang/Throwable
    //   0	116	213	java/sql/SQLException
    //   119	213	213	java/sql/SQLException
  }
  
  public static Transaction getTransaction(byte[] paramArrayOfByte)
    throws NxtException.ValidationException
  {
    try
    {
      ByteBuffer localByteBuffer = ByteBuffer.wrap(paramArrayOfByte);
      localByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
      
      byte b1 = localByteBuffer.get();
      byte b2 = localByteBuffer.get();
      int i = localByteBuffer.getInt();
      short s = localByteBuffer.getShort();
      byte[] arrayOfByte1 = new byte[32];
      localByteBuffer.get(arrayOfByte1);
      Long localLong1 = Long.valueOf(localByteBuffer.getLong());
      int j = localByteBuffer.getInt();
      int k = localByteBuffer.getInt();
      Long localLong2 = Convert.zeroToNull(localByteBuffer.getLong());
      byte[] arrayOfByte2 = new byte[64];
      localByteBuffer.get(arrayOfByte2);
      
      Type localType = findTransactionType(b1, b2);
      Transaction localTransaction = new Transaction(localType, i, s, arrayOfByte1, localLong1, j, k, localLong2, arrayOfByte2);
      try
      {
        localType.loadAttachment(localTransaction, localByteBuffer);
      }
      catch (NxtException.ValidationException localValidationException)
      {
        if (localTransaction.attachment != null) {
          Logger.logDebugMessage("Invalid transaction attachment:\n" + localTransaction.attachment.getJSON());
        }
        throw localValidationException;
      }
      return localTransaction;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw new NxtException.ValidationException(localRuntimeException.toString());
    }
  }
  
  static Transaction getTransaction(JSONObject paramJSONObject)
    throws NxtException.ValidationException
  {
    try
    {
      byte b1 = ((Long)paramJSONObject.get("type")).byteValue();
      byte b2 = ((Long)paramJSONObject.get("subtype")).byteValue();
      int i = ((Long)paramJSONObject.get("timestamp")).intValue();
      short s = ((Long)paramJSONObject.get("deadline")).shortValue();
      byte[] arrayOfByte1 = Convert.parseHexString((String)paramJSONObject.get("senderPublicKey"));
      Long localLong1 = Convert.parseUnsignedLong((String)paramJSONObject.get("recipient"));
      if (localLong1 == null) {
        localLong1 = Long.valueOf(0L);
      }
      int j = ((Long)paramJSONObject.get("amount")).intValue();
      int k = ((Long)paramJSONObject.get("fee")).intValue();
      Long localLong2 = Convert.parseUnsignedLong((String)paramJSONObject.get("referencedTransaction"));
      byte[] arrayOfByte2 = Convert.parseHexString((String)paramJSONObject.get("signature"));
      
      Type localType = findTransactionType(b1, b2);
      Transaction localTransaction = new Transaction(localType, i, s, arrayOfByte1, localLong1, j, k, localLong2, arrayOfByte2);
      

      JSONObject localJSONObject = (JSONObject)paramJSONObject.get("attachment");
      try
      {
        localType.loadAttachment(localTransaction, localJSONObject);
      }
      catch (NxtException.ValidationException localValidationException)
      {
        Logger.logDebugMessage("Invalid transaction attachment:\n" + localJSONObject.toJSONString());
        throw localValidationException;
      }
      return localTransaction;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw new NxtException.ValidationException(localRuntimeException.toString());
    }
  }
  
  static Transaction getTransaction(Connection paramConnection, ResultSet paramResultSet)
    throws NxtException.ValidationException
  {
    try
    {
      byte b1 = paramResultSet.getByte("type");
      byte b2 = paramResultSet.getByte("subtype");
      int i = paramResultSet.getInt("timestamp");
      short s = paramResultSet.getShort("deadline");
      byte[] arrayOfByte1 = paramResultSet.getBytes("sender_public_key");
      Long localLong1 = Long.valueOf(paramResultSet.getLong("recipient_id"));
      int j = paramResultSet.getInt("amount");
      int k = paramResultSet.getInt("fee");
      Long localLong2 = Long.valueOf(paramResultSet.getLong("referenced_transaction_id"));
      if (paramResultSet.wasNull()) {
        localLong2 = null;
      }
      byte[] arrayOfByte2 = paramResultSet.getBytes("signature");
      
      Type localType = findTransactionType(b1, b2);
      Transaction localTransaction = new Transaction(localType, i, s, arrayOfByte1, localLong1, j, k, localLong2, arrayOfByte2);
      
      localTransaction.blockId = Long.valueOf(paramResultSet.getLong("block_id"));
      localTransaction.height = paramResultSet.getInt("height");
      localTransaction.id = Long.valueOf(paramResultSet.getLong("id"));
      localTransaction.senderId = Long.valueOf(paramResultSet.getLong("sender_id"));
      
      localTransaction.attachment = ((Attachment)paramResultSet.getObject("attachment"));
      
      return localTransaction;
    }
    catch (SQLException localSQLException)
    {
      throw new RuntimeException(localSQLException.toString(), localSQLException);
    }
  }
  
  /* Error */
  static List<Transaction> findBlockTransactions(Connection paramConnection, Long paramLong)
  {
    // Byte code:
    //   0: new 96	java/util/ArrayList
    //   3: dup
    //   4: invokespecial 97	java/util/ArrayList:<init>	()V
    //   7: astore_2
    //   8: aload_0
    //   9: ldc 98
    //   11: invokeinterface 12 2 0
    //   16: astore_3
    //   17: aconst_null
    //   18: astore 4
    //   20: aload_3
    //   21: iconst_1
    //   22: aload_1
    //   23: invokevirtual 13	java/lang/Long:longValue	()J
    //   26: invokeinterface 14 4 0
    //   31: aload_3
    //   32: invokeinterface 15 1 0
    //   37: astore 5
    //   39: aload 5
    //   41: invokeinterface 16 1 0
    //   46: ifeq +19 -> 65
    //   49: aload_2
    //   50: aload_0
    //   51: aload 5
    //   53: invokestatic 17	nxt/Transaction:getTransaction	(Ljava/sql/Connection;Ljava/sql/ResultSet;)Lnxt/Transaction;
    //   56: invokeinterface 99 2 0
    //   61: pop
    //   62: goto -23 -> 39
    //   65: aload 5
    //   67: invokeinterface 18 1 0
    //   72: aload_2
    //   73: astore 6
    //   75: aload_3
    //   76: ifnull +35 -> 111
    //   79: aload 4
    //   81: ifnull +24 -> 105
    //   84: aload_3
    //   85: invokeinterface 19 1 0
    //   90: goto +21 -> 111
    //   93: astore 7
    //   95: aload 4
    //   97: aload 7
    //   99: invokevirtual 21	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   102: goto +9 -> 111
    //   105: aload_3
    //   106: invokeinterface 19 1 0
    //   111: aload 6
    //   113: areturn
    //   114: astore 5
    //   116: aload 5
    //   118: astore 4
    //   120: aload 5
    //   122: athrow
    //   123: astore 8
    //   125: aload_3
    //   126: ifnull +35 -> 161
    //   129: aload 4
    //   131: ifnull +24 -> 155
    //   134: aload_3
    //   135: invokeinterface 19 1 0
    //   140: goto +21 -> 161
    //   143: astore 9
    //   145: aload 4
    //   147: aload 9
    //   149: invokevirtual 21	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   152: goto +9 -> 161
    //   155: aload_3
    //   156: invokeinterface 19 1 0
    //   161: aload 8
    //   163: athrow
    //   164: astore_3
    //   165: new 24	java/lang/RuntimeException
    //   168: dup
    //   169: aload_3
    //   170: invokevirtual 95	java/sql/SQLException:toString	()Ljava/lang/String;
    //   173: aload_3
    //   174: invokespecial 26	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   177: athrow
    //   178: astore_3
    //   179: new 24	java/lang/RuntimeException
    //   182: dup
    //   183: new 28	java/lang/StringBuilder
    //   186: dup
    //   187: invokespecial 29	java/lang/StringBuilder:<init>	()V
    //   190: ldc 100
    //   192: invokevirtual 31	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   195: aload_1
    //   196: invokevirtual 32	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   199: ldc 101
    //   201: invokevirtual 31	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   204: invokevirtual 34	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   207: invokespecial 35	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   210: athrow
    // Line number table:
    //   Java source line #205	-> byte code offset #0
    //   Java source line #206	-> byte code offset #8
    //   Java source line #207	-> byte code offset #20
    //   Java source line #208	-> byte code offset #31
    //   Java source line #209	-> byte code offset #39
    //   Java source line #210	-> byte code offset #49
    //   Java source line #212	-> byte code offset #65
    //   Java source line #213	-> byte code offset #72
    //   Java source line #214	-> byte code offset #75
    //   Java source line #206	-> byte code offset #114
    //   Java source line #214	-> byte code offset #123
    //   Java source line #215	-> byte code offset #165
    //   Java source line #216	-> byte code offset #178
    //   Java source line #217	-> byte code offset #179
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	211	0	paramConnection	Connection
    //   0	211	1	paramLong	Long
    //   7	66	2	localArrayList1	java.util.ArrayList
    //   16	140	3	localPreparedStatement	PreparedStatement
    //   164	10	3	localSQLException	SQLException
    //   178	1	3	localValidationException	NxtException.ValidationException
    //   18	128	4	localObject1	Object
    //   37	29	5	localResultSet	ResultSet
    //   114	7	5	localThrowable1	Throwable
    //   93	5	7	localThrowable2	Throwable
    //   123	39	8	localObject2	Object
    //   143	5	9	localThrowable3	Throwable
    // Exception table:
    //   from	to	target	type
    //   84	90	93	java/lang/Throwable
    //   20	75	114	java/lang/Throwable
    //   20	75	123	finally
    //   114	125	123	finally
    //   134	140	143	java/lang/Throwable
    //   8	111	164	java/sql/SQLException
    //   114	164	164	java/sql/SQLException
    //   8	111	178	nxt/NxtException$ValidationException
    //   114	164	178	nxt/NxtException$ValidationException
  }
  
  static void saveTransactions(Connection paramConnection, List<Transaction> paramList)
  {
    try
    {
      for (Transaction localTransaction : paramList)
      {
        PreparedStatement localPreparedStatement = paramConnection.prepareStatement("INSERT INTO transaction (id, deadline, sender_public_key, recipient_id, amount, fee, referenced_transaction_id, height, block_id, signature, timestamp, type, subtype, sender_id, attachment)  VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");Object localObject1 = null;
        try
        {
          int i = 0;
          localPreparedStatement.setLong(++i, localTransaction.getId().longValue());
          localPreparedStatement.setShort(++i, localTransaction.deadline);
          localPreparedStatement.setBytes(++i, localTransaction.senderPublicKey);
          localPreparedStatement.setLong(++i, localTransaction.recipientId.longValue());
          localPreparedStatement.setInt(++i, localTransaction.amount);
          localPreparedStatement.setInt(++i, localTransaction.fee);
          if (localTransaction.referencedTransactionId != null) {
            localPreparedStatement.setLong(++i, localTransaction.referencedTransactionId.longValue());
          } else {
            localPreparedStatement.setNull(++i, -5);
          }
          localPreparedStatement.setInt(++i, localTransaction.height);
          localPreparedStatement.setLong(++i, localTransaction.blockId.longValue());
          localPreparedStatement.setBytes(++i, localTransaction.signature);
          localPreparedStatement.setInt(++i, localTransaction.timestamp);
          localPreparedStatement.setByte(++i, localTransaction.type.getType());
          localPreparedStatement.setByte(++i, localTransaction.type.getSubtype());
          localPreparedStatement.setLong(++i, localTransaction.getSenderId().longValue());
          if (localTransaction.attachment != null) {
            localPreparedStatement.setObject(++i, localTransaction.attachment);
          } else {
            localPreparedStatement.setNull(++i, 2000);
          }
          localPreparedStatement.executeUpdate();
        }
        catch (Throwable localThrowable2)
        {
          localObject1 = localThrowable2;throw localThrowable2;
        }
        finally
        {
          if (localPreparedStatement != null) {
            if (localObject1 != null) {
              try
              {
                localPreparedStatement.close();
              }
              catch (Throwable localThrowable3)
              {
                localObject1.addSuppressed(localThrowable3);
              }
            } else {
              localPreparedStatement.close();
            }
          }
        }
      }
    }
    catch (SQLException localSQLException)
    {
      throw new RuntimeException(localSQLException.toString(), localSQLException);
    }
  }
  
  private int height = 2147483647;
  private Long blockId;
  private volatile Block block;
  private byte[] signature;
  private int timestamp;
  private Attachment attachment;
  private volatile Long id;
  private volatile String stringId = null;
  private volatile Long senderId;
  private volatile String hash;
  private static final int TRANSACTION_BYTES_LENGTH = 128;
  
  private Transaction(Type paramType, int paramInt1, short paramShort, byte[] paramArrayOfByte1, Long paramLong1, int paramInt2, int paramInt3, Long paramLong2, byte[] paramArrayOfByte2)
    throws NxtException.ValidationException
  {
    if ((paramInt1 == 0) && (Arrays.equals(paramArrayOfByte1, Genesis.CREATOR_PUBLIC_KEY)) ? (paramShort == 0) || (paramInt3 == 0) : (paramShort < 1) || (paramInt3 <= 0) || (paramInt3 > 1000000000L) || (paramInt2 < 0) || (paramInt2 > 1000000000L) || (paramType == null)) {
      throw new NxtException.ValidationException("Invalid transaction parameters:\n type: " + paramType + ", timestamp: " + paramInt1 + ", deadline: " + paramShort + ", fee: " + paramInt3 + ", amount: " + paramInt2);
    }
    this.timestamp = paramInt1;
    this.deadline = paramShort;
    this.senderPublicKey = paramArrayOfByte1;
    this.recipientId = paramLong1;
    this.amount = paramInt2;
    this.fee = paramInt3;
    this.referencedTransactionId = paramLong2;
    this.signature = paramArrayOfByte2;
    this.type = paramType;
  }
  
  public short getDeadline()
  {
    return this.deadline;
  }
  
  public byte[] getSenderPublicKey()
  {
    return this.senderPublicKey;
  }
  
  public Long getRecipientId()
  {
    return this.recipientId;
  }
  
  public int getAmount()
  {
    return this.amount;
  }
  
  public int getFee()
  {
    return this.fee;
  }
  
  public Long getReferencedTransactionId()
  {
    return this.referencedTransactionId;
  }
  
  public int getHeight()
  {
    return this.height;
  }
  
  public byte[] getSignature()
  {
    return this.signature;
  }
  
  public Type getType()
  {
    return this.type;
  }
  
  public Block getBlock()
  {
    if (this.block == null) {
      this.block = Block.findBlock(this.blockId);
    }
    return this.block;
  }
  
  void setBlock(Block paramBlock)
  {
    this.block = paramBlock;
    this.blockId = paramBlock.getId();
    this.height = paramBlock.getHeight();
  }
  
  public int getTimestamp()
  {
    return this.timestamp;
  }
  
  public int getExpiration()
  {
    return this.timestamp + this.deadline * 60;
  }
  
  public Attachment getAttachment()
  {
    return this.attachment;
  }
  
  public Long getId()
  {
    if (this.id == null)
    {
      if (this.signature == null) {
        throw new IllegalStateException("Transaction is not signed yet");
      }
      byte[] arrayOfByte = Crypto.sha256().digest(getBytes());
      BigInteger localBigInteger = new BigInteger(1, new byte[] { arrayOfByte[7], arrayOfByte[6], arrayOfByte[5], arrayOfByte[4], arrayOfByte[3], arrayOfByte[2], arrayOfByte[1], arrayOfByte[0] });
      this.id = Long.valueOf(localBigInteger.longValue());
      this.stringId = localBigInteger.toString();
    }
    return this.id;
  }
  
  public String getStringId()
  {
    if (this.stringId == null)
    {
      getId();
      if (this.stringId == null) {
        this.stringId = Convert.toUnsignedLong(this.id);
      }
    }
    return this.stringId;
  }
  
  public Long getSenderId()
  {
    if (this.senderId == null) {
      this.senderId = Account.getId(this.senderPublicKey);
    }
    return this.senderId;
  }
  
  public int compareTo(Transaction paramTransaction)
  {
    if (this.height < paramTransaction.height) {
      return -1;
    }
    if (this.height > paramTransaction.height) {
      return 1;
    }
    if (this.fee * paramTransaction.getSize() > paramTransaction.fee * getSize()) {
      return -1;
    }
    if (this.fee * paramTransaction.getSize() < paramTransaction.fee * getSize()) {
      return 1;
    }
    if (this.timestamp < paramTransaction.timestamp) {
      return -1;
    }
    if (this.timestamp > paramTransaction.timestamp) {
      return 1;
    }
    return 0;
  }
  
  public byte[] getBytes()
  {
    ByteBuffer localByteBuffer = ByteBuffer.allocate(getSize());
    localByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    localByteBuffer.put(this.type.getType());
    localByteBuffer.put(this.type.getSubtype());
    localByteBuffer.putInt(this.timestamp);
    localByteBuffer.putShort(this.deadline);
    localByteBuffer.put(this.senderPublicKey);
    localByteBuffer.putLong(Convert.nullToZero(this.recipientId));
    localByteBuffer.putInt(this.amount);
    localByteBuffer.putInt(this.fee);
    localByteBuffer.putLong(Convert.nullToZero(this.referencedTransactionId));
    localByteBuffer.put(this.signature);
    if (this.attachment != null) {
      localByteBuffer.put(this.attachment.getBytes());
    }
    return localByteBuffer.array();
  }
  
  public JSONObject getJSONObject()
  {
    JSONObject localJSONObject = new JSONObject();
    
    localJSONObject.put("type", Byte.valueOf(this.type.getType()));
    localJSONObject.put("subtype", Byte.valueOf(this.type.getSubtype()));
    localJSONObject.put("timestamp", Integer.valueOf(this.timestamp));
    localJSONObject.put("deadline", Short.valueOf(this.deadline));
    localJSONObject.put("senderPublicKey", Convert.toHexString(this.senderPublicKey));
    localJSONObject.put("recipient", Convert.toUnsignedLong(this.recipientId));
    localJSONObject.put("amount", Integer.valueOf(this.amount));
    localJSONObject.put("fee", Integer.valueOf(this.fee));
    localJSONObject.put("referencedTransaction", Convert.toUnsignedLong(this.referencedTransactionId));
    localJSONObject.put("signature", Convert.toHexString(this.signature));
    if (this.attachment != null) {
      localJSONObject.put("attachment", this.attachment.getJSON());
    }
    return localJSONObject;
  }
  
  public void sign(String paramString)
  {
    if (this.signature != null) {
      throw new IllegalStateException("Transaction already signed");
    }
    this.signature = new byte[64];
    this.signature = Crypto.sign(getBytes(), paramString);
    try
    {
      while (!verify())
      {
        this.timestamp += 1;
        
        this.signature = new byte[64];
        this.signature = Crypto.sign(getBytes(), paramString);
      }
    }
    catch (RuntimeException localRuntimeException)
    {
      Logger.logMessage("Error signing transaction", localRuntimeException);
    }
  }
  
  public String getHash()
  {
    if (this.hash == null)
    {
      byte[] arrayOfByte = getBytes();
      for (int i = 64; i < 128; i++) {
        arrayOfByte[i] = 0;
      }
      this.hash = Convert.toHexString(Crypto.sha256().digest(arrayOfByte));
    }
    return this.hash;
  }
  
  public boolean equals(Object paramObject)
  {
    return ((paramObject instanceof Transaction)) && (getId().equals(((Transaction)paramObject).getId()));
  }
  
  public int hashCode()
  {
    return getId().hashCode();
  }
  
  boolean verify()
  {
    Account localAccount = Account.getAccount(getSenderId());
    if (localAccount == null) {
      return false;
    }
    byte[] arrayOfByte = getBytes();
    for (int i = 64; i < 128; i++) {
      arrayOfByte[i] = 0;
    }
    return (Crypto.verify(this.signature, arrayOfByte, this.senderPublicKey)) && (localAccount.setOrVerify(this.senderPublicKey, getHeight()));
  }
  
  void validateAttachment()
    throws NxtException.ValidationException
  {
    this.type.validateAttachment(this);
  }
  
  boolean isDoubleSpending()
  {
    Account localAccount = Account.getAccount(getSenderId());
    if (localAccount == null) {
      return true;
    }
    synchronized (localAccount)
    {
      return this.type.isDoubleSpending(this, localAccount, this.amount + this.fee);
    }
  }
  
  void apply()
  {
    Account localAccount1 = Account.getAccount(getSenderId());
    if (!localAccount1.setOrVerify(this.senderPublicKey, getHeight())) {
      throw new RuntimeException("sender public key mismatch");
    }
    localAccount1.apply(getHeight());
    Blockchain.transactionHashes.put(getHash(), this);
    Account localAccount2 = Account.getAccount(this.recipientId);
    if (localAccount2 == null) {
      localAccount2 = Account.addOrGetAccount(this.recipientId);
    }
    localAccount1.addToBalanceAndUnconfirmedBalance(-(this.amount + this.fee) * 100L);
    this.type.apply(this, localAccount1, localAccount2);
  }
  
  void undo()
    throws Transaction.UndoNotSupportedException
  {
    Account localAccount1 = Account.getAccount(this.senderId);
    localAccount1.undo(getHeight());
    localAccount1.addToBalance((this.amount + this.fee) * 100L);
    Account localAccount2 = Account.getAccount(this.recipientId);
    this.type.undo(this, localAccount1, localAccount2);
  }
  
  void updateTotals(Map<Long, Long> paramMap, Map<Long, Map<Long, Long>> paramMap1)
  {
    Long localLong1 = getSenderId();
    Long localLong2 = (Long)paramMap.get(localLong1);
    if (localLong2 == null) {
      localLong2 = Long.valueOf(0L);
    }
    paramMap.put(localLong1, Long.valueOf(localLong2.longValue() + (this.amount + this.fee) * 100L));
    this.type.updateTotals(this, paramMap, paramMap1, localLong2);
  }
  
  boolean isDuplicate(Map<Type, Set<String>> paramMap)
  {
    return this.type.isDuplicate(this, paramMap);
  }
  
  int getSize()
  {
    return 128 + (this.attachment == null ? 0 : this.attachment.getSize());
  }
  
  public static Type findTransactionType(byte paramByte1, byte paramByte2)
  {
    switch (paramByte1)
    {
    case 0: 
      switch (paramByte2)
      {
      case 0: 
        return Transaction.Type.Payment.ORDINARY;
      }
      return null;
    case 1: 
      switch (paramByte2)
      {
      case 0: 
        return Transaction.Type.Messaging.ARBITRARY_MESSAGE;
      case 1: 
        return Transaction.Type.Messaging.ALIAS_ASSIGNMENT;
      case 2: 
        return Transaction.Type.Messaging.POLL_CREATION;
      case 3: 
        return Transaction.Type.Messaging.VOTE_CASTING;
      }
      return null;
    case 2: 
      switch (paramByte2)
      {
      case 0: 
        return Transaction.Type.ColoredCoins.ASSET_ISSUANCE;
      case 1: 
        return Transaction.Type.ColoredCoins.ASSET_TRANSFER;
      case 2: 
        return Transaction.Type.ColoredCoins.ASK_ORDER_PLACEMENT;
      case 3: 
        return Transaction.Type.ColoredCoins.BID_ORDER_PLACEMENT;
      case 4: 
        return Transaction.Type.ColoredCoins.ASK_ORDER_CANCELLATION;
      case 5: 
        return Transaction.Type.ColoredCoins.BID_ORDER_CANCELLATION;
      }
      return null;
    }
    return null;
  }
  
  public static abstract class Type
  {
    public abstract byte getType();
    
    public abstract byte getSubtype();
    
    abstract void loadAttachment(Transaction paramTransaction, ByteBuffer paramByteBuffer)
      throws NxtException.ValidationException;
    
    abstract void loadAttachment(Transaction paramTransaction, JSONObject paramJSONObject)
      throws NxtException.ValidationException;
    
    abstract void validateAttachment(Transaction paramTransaction)
      throws NxtException.ValidationException;
    
    final boolean isDoubleSpending(Transaction paramTransaction, Account paramAccount, int paramInt)
    {
      if (paramAccount.getUnconfirmedBalance() < paramInt * 100L) {
        return true;
      }
      paramAccount.addToUnconfirmedBalance(-paramInt * 100L);
      return checkDoubleSpending(paramTransaction, paramAccount, paramInt);
    }
    
    abstract boolean checkDoubleSpending(Transaction paramTransaction, Account paramAccount, int paramInt);
    
    abstract void apply(Transaction paramTransaction, Account paramAccount1, Account paramAccount2);
    
    abstract void undo(Transaction paramTransaction, Account paramAccount1, Account paramAccount2)
      throws Transaction.UndoNotSupportedException;
    
    abstract void updateTotals(Transaction paramTransaction, Map<Long, Long> paramMap, Map<Long, Map<Long, Long>> paramMap1, Long paramLong);
    
    boolean isDuplicate(Transaction paramTransaction, Map<Type, Set<String>> paramMap)
    {
      return false;
    }
    
    public static abstract class Payment
      extends Transaction.Type
    {
      public final byte getType()
      {
        return 0;
      }
      
      public static final Transaction.Type ORDINARY = new Payment()
      {
        public final byte getSubtype()
        {
          return 0;
        }
        
        void loadAttachment(Transaction paramAnonymousTransaction, ByteBuffer paramAnonymousByteBuffer)
          throws NxtException.ValidationException
        {
          validateAttachment(paramAnonymousTransaction);
        }
        
        void loadAttachment(Transaction paramAnonymousTransaction, JSONObject paramAnonymousJSONObject)
          throws NxtException.ValidationException
        {
          validateAttachment(paramAnonymousTransaction);
        }
        
        void apply(Transaction paramAnonymousTransaction, Account paramAnonymousAccount1, Account paramAnonymousAccount2)
        {
          paramAnonymousAccount2.addToBalanceAndUnconfirmedBalance(paramAnonymousTransaction.amount * 100L);
        }
        
        void undo(Transaction paramAnonymousTransaction, Account paramAnonymousAccount1, Account paramAnonymousAccount2)
        {
          paramAnonymousAccount2.addToBalanceAndUnconfirmedBalance(-paramAnonymousTransaction.amount * 100L);
        }
        
        void updateTotals(Transaction paramAnonymousTransaction, Map<Long, Long> paramAnonymousMap, Map<Long, Map<Long, Long>> paramAnonymousMap1, Long paramAnonymousLong) {}
        
        boolean checkDoubleSpending(Transaction paramAnonymousTransaction, Account paramAnonymousAccount, int paramAnonymousInt)
        {
          return false;
        }
        
        void validateAttachment(Transaction paramAnonymousTransaction)
          throws NxtException.ValidationException
        {
          if ((paramAnonymousTransaction.amount <= 0) || (paramAnonymousTransaction.amount >= 1000000000L)) {
            throw new NxtException.ValidationException("Invalid payment transaction amount: " + paramAnonymousTransaction.amount);
          }
        }
      };
    }
    
    public static abstract class Messaging
      extends Transaction.Type
    {
      public final byte getType()
      {
        return 1;
      }
      
      boolean checkDoubleSpending(Transaction paramTransaction, Account paramAccount, int paramInt)
      {
        return false;
      }
      
      public static final Transaction.Type ARBITRARY_MESSAGE = new Messaging()
      {
        public final byte getSubtype()
        {
          return 0;
        }
        
        void loadAttachment(Transaction paramAnonymousTransaction, ByteBuffer paramAnonymousByteBuffer)
          throws NxtException.ValidationException
        {
          int i = paramAnonymousByteBuffer.getInt();
          if (i > 1000) {
            throw new NxtException.ValidationException("Invalid message length: " + i);
          }
          byte[] arrayOfByte = new byte[i];
          paramAnonymousByteBuffer.get(arrayOfByte);
          paramAnonymousTransaction.attachment = new Attachment.MessagingArbitraryMessage(arrayOfByte);
          validateAttachment(paramAnonymousTransaction);
        }
        
        void loadAttachment(Transaction paramAnonymousTransaction, JSONObject paramAnonymousJSONObject)
          throws NxtException.ValidationException
        {
          String str = (String)paramAnonymousJSONObject.get("message");
          paramAnonymousTransaction.attachment = new Attachment.MessagingArbitraryMessage(Convert.parseHexString(str));
          validateAttachment(paramAnonymousTransaction);
        }
        
        void apply(Transaction paramAnonymousTransaction, Account paramAnonymousAccount1, Account paramAnonymousAccount2) {}
        
        void undo(Transaction paramAnonymousTransaction, Account paramAnonymousAccount1, Account paramAnonymousAccount2) {}
        
        void validateAttachment(Transaction paramAnonymousTransaction)
          throws NxtException.ValidationException
        {
          if (Blockchain.getLastBlock().getHeight() < 40000) {
            throw new Transaction.NotYetEnabledException("Arbitrary messages not yet enabled at height " + Blockchain.getLastBlock().getHeight());
          }
          Attachment.MessagingArbitraryMessage localMessagingArbitraryMessage = (Attachment.MessagingArbitraryMessage)paramAnonymousTransaction.attachment;
          if ((paramAnonymousTransaction.amount != 0) || (localMessagingArbitraryMessage.getMessage().length > 1000)) {
            throw new NxtException.ValidationException("Invalid transaction amount or message length");
          }
        }
      };
      public static final Transaction.Type ALIAS_ASSIGNMENT = new Messaging()
      {
        public final byte getSubtype()
        {
          return 1;
        }
        
        void loadAttachment(Transaction paramAnonymousTransaction, ByteBuffer paramAnonymousByteBuffer)
          throws NxtException.ValidationException
        {
          int i = paramAnonymousByteBuffer.get();
          if (i > 300) {
            throw new NxtException.ValidationException("Max alias length exceeded");
          }
          byte[] arrayOfByte1 = new byte[i];
          paramAnonymousByteBuffer.get(arrayOfByte1);
          int j = paramAnonymousByteBuffer.getShort();
          if (j > 3000) {
            throw new NxtException.ValidationException("Max alias URI length exceeded");
          }
          byte[] arrayOfByte2 = new byte[j];
          paramAnonymousByteBuffer.get(arrayOfByte2);
          try
          {
            paramAnonymousTransaction.attachment = new Attachment.MessagingAliasAssignment(new String(arrayOfByte1, "UTF-8"), new String(arrayOfByte2, "UTF-8"));
            
            validateAttachment(paramAnonymousTransaction);
          }
          catch (RuntimeException|UnsupportedEncodingException localRuntimeException)
          {
            Logger.logDebugMessage("Error parsing alias assignment", localRuntimeException);
            throw new NxtException.ValidationException(localRuntimeException.toString());
          }
        }
        
        void loadAttachment(Transaction paramAnonymousTransaction, JSONObject paramAnonymousJSONObject)
          throws NxtException.ValidationException
        {
          String str1 = (String)paramAnonymousJSONObject.get("alias");
          String str2 = (String)paramAnonymousJSONObject.get("uri");
          paramAnonymousTransaction.attachment = new Attachment.MessagingAliasAssignment(str1, str2);
          validateAttachment(paramAnonymousTransaction);
        }
        
        void apply(Transaction paramAnonymousTransaction, Account paramAnonymousAccount1, Account paramAnonymousAccount2)
        {
          Attachment.MessagingAliasAssignment localMessagingAliasAssignment = (Attachment.MessagingAliasAssignment)paramAnonymousTransaction.attachment;
          Block localBlock = paramAnonymousTransaction.getBlock();
          Alias.addOrUpdateAlias(paramAnonymousAccount1, paramAnonymousTransaction.getId(), localMessagingAliasAssignment.getAliasName(), localMessagingAliasAssignment.getAliasURI(), localBlock.getTimestamp());
        }
        
        void undo(Transaction paramAnonymousTransaction, Account paramAnonymousAccount1, Account paramAnonymousAccount2)
          throws Transaction.UndoNotSupportedException
        {
          throw new Transaction.UndoNotSupportedException(paramAnonymousTransaction, "Reversal of alias assignment not supported");
        }
        
        boolean isDuplicate(Transaction paramAnonymousTransaction, Map<Transaction.Type, Set<String>> paramAnonymousMap)
        {
          Object localObject = (Set)paramAnonymousMap.get(this);
          if (localObject == null)
          {
            localObject = new HashSet();
            paramAnonymousMap.put(this, localObject);
          }
          Attachment.MessagingAliasAssignment localMessagingAliasAssignment = (Attachment.MessagingAliasAssignment)paramAnonymousTransaction.attachment;
          return !((Set)localObject).add(localMessagingAliasAssignment.getAliasName().toLowerCase());
        }
        
        void validateAttachment(Transaction paramAnonymousTransaction)
          throws NxtException.ValidationException
        {
          if (Blockchain.getLastBlock().getHeight() < 22000) {
            throw new Transaction.NotYetEnabledException("Aliases not yet enabled at height " + Blockchain.getLastBlock().getHeight());
          }
          Attachment.MessagingAliasAssignment localMessagingAliasAssignment = (Attachment.MessagingAliasAssignment)paramAnonymousTransaction.attachment;
          if ((!Genesis.CREATOR_ID.equals(paramAnonymousTransaction.recipientId)) || (paramAnonymousTransaction.amount != 0) || (localMessagingAliasAssignment.getAliasName().length() == 0) || (localMessagingAliasAssignment.getAliasName().length() > 100) || (localMessagingAliasAssignment.getAliasURI().length() > 1000)) {
            throw new NxtException.ValidationException("Invalid alias assignment transaction");
          }
          String str = localMessagingAliasAssignment.getAliasName().toLowerCase();
          for (int i = 0; i < str.length(); i++) {
            if ("0123456789abcdefghijklmnopqrstuvwxyz".indexOf(str.charAt(i)) < 0) {
              throw new NxtException.ValidationException("Invalid characters in alias name: " + str);
            }
          }
          Alias localAlias = Alias.getAlias(str);
          if ((localAlias != null) && (!Arrays.equals(localAlias.getAccount().getPublicKey(), paramAnonymousTransaction.senderPublicKey))) {
            throw new NxtException.ValidationException("Alias " + str + " already owned by another account");
          }
        }
      };
      public static final Transaction.Type POLL_CREATION = new Messaging()
      {
        public final byte getSubtype()
        {
          return 2;
        }
        
        void loadAttachment(Transaction paramAnonymousTransaction, ByteBuffer paramAnonymousByteBuffer)
          throws NxtException.ValidationException
        {
          byte[] arrayOfByte1;
          String str1;
          try
          {
            int i = paramAnonymousByteBuffer.getShort();
            if (i > 400) {
              throw new NxtException.ValidationException("Error parsing poll name");
            }
            arrayOfByte1 = new byte[i];
            paramAnonymousByteBuffer.get(arrayOfByte1);
            str1 = new String(arrayOfByte1, "UTF-8").trim();
          }
          catch (RuntimeException|UnsupportedEncodingException localRuntimeException1)
          {
            Logger.logDebugMessage("Error parsing poll name", localRuntimeException1);
            throw new NxtException.ValidationException(localRuntimeException1.toString());
          }
          String str2;
          try
          {
            int j = paramAnonymousByteBuffer.getShort();
            if (j > 4000) {
              throw new NxtException.ValidationException("Error parsing poll description");
            }
            arrayOfByte1 = new byte[j];
            paramAnonymousByteBuffer.get(arrayOfByte1);
            str2 = new String(arrayOfByte1, "UTF-8").trim();
          }
          catch (RuntimeException|UnsupportedEncodingException localRuntimeException2)
          {
            Logger.logDebugMessage("Error parsing poll description", localRuntimeException2);
            throw new NxtException.ValidationException(localRuntimeException2.toString());
          }
          String[] arrayOfString;
          try
          {
            int k = paramAnonymousByteBuffer.get();
            arrayOfString = new String[k];
            for (int m = 0; m < k; m++)
            {
              int n = paramAnonymousByteBuffer.getShort();
              if (n > 400) {
                throw new NxtException.ValidationException("Error parsing poll options");
              }
              byte[] arrayOfByte2 = new byte[n];
              paramAnonymousByteBuffer.get(arrayOfByte2);
              arrayOfString[m] = new String(arrayOfByte2, "UTF-8").trim();
            }
          }
          catch (RuntimeException|UnsupportedEncodingException localRuntimeException3)
          {
            Logger.logDebugMessage("Error parsing poll options", localRuntimeException3);
            throw new NxtException.ValidationException(localRuntimeException3.toString());
          }
          byte b1;
          byte b2;
          boolean bool;
          try
          {
            b1 = paramAnonymousByteBuffer.get();
            b2 = paramAnonymousByteBuffer.get();
            bool = paramAnonymousByteBuffer.get() != 0;
          }
          catch (RuntimeException localRuntimeException4)
          {
            Logger.logDebugMessage("Error parsing poll creation parameters", localRuntimeException4);
            throw new NxtException.ValidationException(localRuntimeException4.toString());
          }
          paramAnonymousTransaction.attachment = new Attachment.MessagingPollCreation(str1, str2, arrayOfString, b1, b2, bool);
          
          validateAttachment(paramAnonymousTransaction);
        }
        
        void loadAttachment(Transaction paramAnonymousTransaction, JSONObject paramAnonymousJSONObject)
          throws NxtException.ValidationException
        {
          String str1 = ((String)paramAnonymousJSONObject.get("name")).trim();
          String str2 = ((String)paramAnonymousJSONObject.get("description")).trim();
          JSONArray localJSONArray = (JSONArray)paramAnonymousJSONObject.get("options");
          String[] arrayOfString = new String[localJSONArray.size()];
          for (int i = 0; i < arrayOfString.length; i++) {
            arrayOfString[i] = ((String)localJSONArray.get(i)).trim();
          }
          i = ((Long)paramAnonymousJSONObject.get("minNumberOfOptions")).byteValue();
          byte b = ((Long)paramAnonymousJSONObject.get("maxNumberOfOptions")).byteValue();
          boolean bool = ((Boolean)paramAnonymousJSONObject.get("optionsAreBinary")).booleanValue();
          
          paramAnonymousTransaction.attachment = new Attachment.MessagingPollCreation(str1, str2, arrayOfString, i, b, bool);
          
          validateAttachment(paramAnonymousTransaction);
        }
        
        void apply(Transaction paramAnonymousTransaction, Account paramAnonymousAccount1, Account paramAnonymousAccount2)
        {
          Attachment.MessagingPollCreation localMessagingPollCreation = (Attachment.MessagingPollCreation)paramAnonymousTransaction.attachment;
          Poll.addPoll(paramAnonymousTransaction.getId(), localMessagingPollCreation.getPollName(), localMessagingPollCreation.getPollDescription(), localMessagingPollCreation.getPollOptions(), localMessagingPollCreation.getMinNumberOfOptions(), localMessagingPollCreation.getMaxNumberOfOptions(), localMessagingPollCreation.isOptionsAreBinary());
        }
        
        void undo(Transaction paramAnonymousTransaction, Account paramAnonymousAccount1, Account paramAnonymousAccount2)
          throws Transaction.UndoNotSupportedException
        {
          throw new Transaction.UndoNotSupportedException(paramAnonymousTransaction, "Reversal of poll creation not supported");
        }
        
        void validateAttachment(Transaction paramAnonymousTransaction)
          throws NxtException.ValidationException
        {
          if (Blockchain.getLastBlock().getHeight() < 222222) {
            throw new Transaction.NotYetEnabledException("Voting System not yet enabled at height " + Blockchain.getLastBlock().getHeight());
          }
          Attachment.MessagingPollCreation localMessagingPollCreation = (Attachment.MessagingPollCreation)paramAnonymousTransaction.attachment;
          for (int i = 0; i < localMessagingPollCreation.getPollOptions().length; i++) {
            if (localMessagingPollCreation.getPollOptions()[i].length() > 100) {
              throw new NxtException.ValidationException("Invalid poll options length");
            }
          }
          if ((localMessagingPollCreation.getPollName().length() > 100) || (localMessagingPollCreation.getPollDescription().length() > 1000) || (localMessagingPollCreation.getPollOptions().length > 100) || (paramAnonymousTransaction.amount != 0) || (!Genesis.CREATOR_ID.equals(paramAnonymousTransaction.recipientId))) {
            throw new NxtException.ValidationException("Invalid poll creation transaction");
          }
        }
      };
      public static final Transaction.Type VOTE_CASTING = new Messaging()
      {
        public final byte getSubtype()
        {
          return 3;
        }
        
        void loadAttachment(Transaction paramAnonymousTransaction, ByteBuffer paramAnonymousByteBuffer)
          throws NxtException.ValidationException
        {
          Long localLong;
          byte[] arrayOfByte;
          try
          {
            localLong = Long.valueOf(paramAnonymousByteBuffer.getLong());
            int i = paramAnonymousByteBuffer.get();
            if (i > 100) {
              throw new NxtException.ValidationException("Error parsing vote casting parameters");
            }
            arrayOfByte = new byte[i];
            paramAnonymousByteBuffer.get(arrayOfByte);
          }
          catch (RuntimeException localRuntimeException)
          {
            throw new NxtException.ValidationException(localRuntimeException.toString());
          }
          paramAnonymousTransaction.attachment = new Attachment.MessagingVoteCasting(localLong, arrayOfByte);
          validateAttachment(paramAnonymousTransaction);
        }
        
        void loadAttachment(Transaction paramAnonymousTransaction, JSONObject paramAnonymousJSONObject)
          throws NxtException.ValidationException
        {
          Long localLong = (Long)paramAnonymousJSONObject.get("pollId");
          JSONArray localJSONArray = (JSONArray)paramAnonymousJSONObject.get("vote");
          byte[] arrayOfByte = new byte[localJSONArray.size()];
          for (int i = 0; i < arrayOfByte.length; i++) {
            arrayOfByte[i] = ((Long)localJSONArray.get(i)).byteValue();
          }
          paramAnonymousTransaction.attachment = new Attachment.MessagingVoteCasting(localLong, arrayOfByte);
          validateAttachment(paramAnonymousTransaction);
        }
        
        void apply(Transaction paramAnonymousTransaction, Account paramAnonymousAccount1, Account paramAnonymousAccount2)
        {
          Attachment.MessagingVoteCasting localMessagingVoteCasting = (Attachment.MessagingVoteCasting)paramAnonymousTransaction.attachment;
          Poll localPoll = Poll.getPoll(localMessagingVoteCasting.getPollId());
          if (localPoll != null)
          {
            Vote localVote = Vote.addVote(paramAnonymousTransaction.getId(), localMessagingVoteCasting.getPollId(), paramAnonymousTransaction.getSenderId(), localMessagingVoteCasting.getPollVote());
            localPoll.addVoter(paramAnonymousTransaction.getSenderId(), localVote.getId());
          }
        }
        
        void undo(Transaction paramAnonymousTransaction, Account paramAnonymousAccount1, Account paramAnonymousAccount2)
          throws Transaction.UndoNotSupportedException
        {
          throw new Transaction.UndoNotSupportedException(paramAnonymousTransaction, "Reversal of vote casting not supported");
        }
        
        void validateAttachment(Transaction paramAnonymousTransaction)
          throws NxtException.ValidationException
        {
          if (Blockchain.getLastBlock().getHeight() < 222222) {
            throw new Transaction.NotYetEnabledException("Voting System not yet enabled at height " + Blockchain.getLastBlock().getHeight());
          }
          if ((paramAnonymousTransaction.amount != 0) || (!Genesis.CREATOR_ID.equals(paramAnonymousTransaction.recipientId))) {
            throw new NxtException.ValidationException("Invalid voting transaction");
          }
        }
      };
      
      void updateTotals(Transaction paramTransaction, Map<Long, Long> paramMap, Map<Long, Map<Long, Long>> paramMap1, Long paramLong) {}
    }
    
    public static abstract class ColoredCoins
      extends Transaction.Type
    {
      public final byte getType()
      {
        return 2;
      }
      
      public static final Transaction.Type ASSET_ISSUANCE = new ColoredCoins()
      {
        public final byte getSubtype()
        {
          return 0;
        }
        
        void loadAttachment(Transaction paramAnonymousTransaction, ByteBuffer paramAnonymousByteBuffer)
          throws NxtException.ValidationException
        {
          int i = paramAnonymousByteBuffer.get();
          if (i > 30) {
            throw new NxtException.ValidationException("Max asset name length exceeded");
          }
          byte[] arrayOfByte1 = new byte[i];
          paramAnonymousByteBuffer.get(arrayOfByte1);
          int j = paramAnonymousByteBuffer.getShort();
          if (j > 300) {
            throw new NxtException.ValidationException("Max asset description length exceeded");
          }
          byte[] arrayOfByte2 = new byte[j];
          paramAnonymousByteBuffer.get(arrayOfByte2);
          int k = paramAnonymousByteBuffer.getInt();
          try
          {
            paramAnonymousTransaction.attachment = new Attachment.ColoredCoinsAssetIssuance(new String(arrayOfByte1, "UTF-8").intern(), new String(arrayOfByte2, "UTF-8").intern(), k);
            
            validateAttachment(paramAnonymousTransaction);
          }
          catch (RuntimeException|UnsupportedEncodingException localRuntimeException)
          {
            throw new NxtException.ValidationException(localRuntimeException.toString());
          }
        }
        
        void loadAttachment(Transaction paramAnonymousTransaction, JSONObject paramAnonymousJSONObject)
          throws NxtException.ValidationException
        {
          String str1 = (String)paramAnonymousJSONObject.get("name");
          String str2 = (String)paramAnonymousJSONObject.get("description");
          int i = ((Long)paramAnonymousJSONObject.get("quantity")).intValue();
          paramAnonymousTransaction.attachment = new Attachment.ColoredCoinsAssetIssuance(str1.trim(), str2.trim(), i);
          validateAttachment(paramAnonymousTransaction);
        }
        
        boolean checkDoubleSpending(Transaction paramAnonymousTransaction, Account paramAnonymousAccount, int paramAnonymousInt)
        {
          return false;
        }
        
        void apply(Transaction paramAnonymousTransaction, Account paramAnonymousAccount1, Account paramAnonymousAccount2)
        {
          Attachment.ColoredCoinsAssetIssuance localColoredCoinsAssetIssuance = (Attachment.ColoredCoinsAssetIssuance)paramAnonymousTransaction.attachment;
          Long localLong = paramAnonymousTransaction.getId();
          Asset.addAsset(localLong, paramAnonymousTransaction.getSenderId(), localColoredCoinsAssetIssuance.getName(), localColoredCoinsAssetIssuance.getDescription(), localColoredCoinsAssetIssuance.getQuantity());
          paramAnonymousAccount1.addToAssetAndUnconfirmedAssetBalance(localLong, localColoredCoinsAssetIssuance.getQuantity());
        }
        
        void undo(Transaction paramAnonymousTransaction, Account paramAnonymousAccount1, Account paramAnonymousAccount2)
        {
          Attachment.ColoredCoinsAssetIssuance localColoredCoinsAssetIssuance = (Attachment.ColoredCoinsAssetIssuance)paramAnonymousTransaction.attachment;
          Long localLong = paramAnonymousTransaction.getId();
          paramAnonymousAccount1.addToAssetAndUnconfirmedAssetBalance(localLong, -localColoredCoinsAssetIssuance.getQuantity());
          Asset.removeAsset(localLong);
        }
        
        void updateTotals(Transaction paramAnonymousTransaction, Map<Long, Long> paramAnonymousMap, Map<Long, Map<Long, Long>> paramAnonymousMap1, Long paramAnonymousLong) {}
        
        boolean isDuplicate(Transaction paramAnonymousTransaction, Map<Transaction.Type, Set<String>> paramAnonymousMap)
        {
          Object localObject = (Set)paramAnonymousMap.get(this);
          if (localObject == null)
          {
            localObject = new HashSet();
            paramAnonymousMap.put(this, localObject);
          }
          Attachment.ColoredCoinsAssetIssuance localColoredCoinsAssetIssuance = (Attachment.ColoredCoinsAssetIssuance)paramAnonymousTransaction.attachment;
          return !((Set)localObject).add(localColoredCoinsAssetIssuance.getName().toLowerCase());
        }
        
        void validateAttachment(Transaction paramAnonymousTransaction)
          throws NxtException.ValidationException
        {
          if (Blockchain.getLastBlock().getHeight() < 111111) {
            throw new Transaction.NotYetEnabledException("Asset Exchange not yet enabled at height " + Blockchain.getLastBlock().getHeight());
          }
          Attachment.ColoredCoinsAssetIssuance localColoredCoinsAssetIssuance = (Attachment.ColoredCoinsAssetIssuance)paramAnonymousTransaction.attachment;
          if ((!Genesis.CREATOR_ID.equals(paramAnonymousTransaction.recipientId)) || (paramAnonymousTransaction.amount != 0) || (paramAnonymousTransaction.fee < 1000) || (localColoredCoinsAssetIssuance.getName().length() < 3) || (localColoredCoinsAssetIssuance.getName().length() > 10) || (localColoredCoinsAssetIssuance.getDescription().length() > 1000) || (localColoredCoinsAssetIssuance.getQuantity() <= 0) || (localColoredCoinsAssetIssuance.getQuantity() > 1000000000L)) {
            throw new NxtException.ValidationException("Invalid asset issue transaction");
          }
          String str = localColoredCoinsAssetIssuance.getName().toLowerCase();
          for (int i = 0; i < str.length(); i++) {
            if ("0123456789abcdefghijklmnopqrstuvwxyz".indexOf(str.charAt(i)) < 0) {
              throw new NxtException.ValidationException("Invalid asset name " + str);
            }
          }
          if (Asset.getAsset(str) != null) {
            throw new NxtException.ValidationException("Asset " + str + " already exists");
          }
        }
      };
      public static final Transaction.Type ASSET_TRANSFER = new ColoredCoins()
      {
        public final byte getSubtype()
        {
          return 1;
        }
        
        void loadAttachment(Transaction paramAnonymousTransaction, ByteBuffer paramAnonymousByteBuffer)
          throws NxtException.ValidationException
        {
          Long localLong = Convert.zeroToNull(paramAnonymousByteBuffer.getLong());
          int i = paramAnonymousByteBuffer.getInt();
          paramAnonymousTransaction.attachment = new Attachment.ColoredCoinsAssetTransfer(localLong, i);
          validateAttachment(paramAnonymousTransaction);
        }
        
        void loadAttachment(Transaction paramAnonymousTransaction, JSONObject paramAnonymousJSONObject)
          throws NxtException.ValidationException
        {
          Long localLong = Convert.parseUnsignedLong((String)paramAnonymousJSONObject.get("asset"));
          int i = ((Long)paramAnonymousJSONObject.get("quantity")).intValue();
          paramAnonymousTransaction.attachment = new Attachment.ColoredCoinsAssetTransfer(localLong, i);
          validateAttachment(paramAnonymousTransaction);
        }
        
        boolean checkDoubleSpending(Transaction paramAnonymousTransaction, Account paramAnonymousAccount, int paramAnonymousInt)
        {
          Attachment.ColoredCoinsAssetTransfer localColoredCoinsAssetTransfer = (Attachment.ColoredCoinsAssetTransfer)paramAnonymousTransaction.attachment;
          Integer localInteger = paramAnonymousAccount.getUnconfirmedAssetBalance(localColoredCoinsAssetTransfer.getAssetId());
          if ((localInteger == null) || (localInteger.intValue() < localColoredCoinsAssetTransfer.getQuantity()))
          {
            paramAnonymousAccount.addToUnconfirmedBalance(paramAnonymousInt * 100L);
            return true;
          }
          paramAnonymousAccount.addToUnconfirmedAssetBalance(localColoredCoinsAssetTransfer.getAssetId(), -localColoredCoinsAssetTransfer.getQuantity());
          return false;
        }
        
        void apply(Transaction paramAnonymousTransaction, Account paramAnonymousAccount1, Account paramAnonymousAccount2)
        {
          Attachment.ColoredCoinsAssetTransfer localColoredCoinsAssetTransfer = (Attachment.ColoredCoinsAssetTransfer)paramAnonymousTransaction.attachment;
          paramAnonymousAccount1.addToAssetAndUnconfirmedAssetBalance(localColoredCoinsAssetTransfer.getAssetId(), -localColoredCoinsAssetTransfer.getQuantity());
          paramAnonymousAccount2.addToAssetAndUnconfirmedAssetBalance(localColoredCoinsAssetTransfer.getAssetId(), localColoredCoinsAssetTransfer.getQuantity());
        }
        
        void undo(Transaction paramAnonymousTransaction, Account paramAnonymousAccount1, Account paramAnonymousAccount2)
        {
          Attachment.ColoredCoinsAssetTransfer localColoredCoinsAssetTransfer = (Attachment.ColoredCoinsAssetTransfer)paramAnonymousTransaction.attachment;
          paramAnonymousAccount1.addToAssetAndUnconfirmedAssetBalance(localColoredCoinsAssetTransfer.getAssetId(), localColoredCoinsAssetTransfer.getQuantity());
          paramAnonymousAccount2.addToAssetAndUnconfirmedAssetBalance(localColoredCoinsAssetTransfer.getAssetId(), -localColoredCoinsAssetTransfer.getQuantity());
        }
        
        void updateTotals(Transaction paramAnonymousTransaction, Map<Long, Long> paramAnonymousMap, Map<Long, Map<Long, Long>> paramAnonymousMap1, Long paramAnonymousLong)
        {
          Attachment.ColoredCoinsAssetTransfer localColoredCoinsAssetTransfer = (Attachment.ColoredCoinsAssetTransfer)paramAnonymousTransaction.attachment;
          Object localObject = (Map)paramAnonymousMap1.get(paramAnonymousTransaction.getSenderId());
          if (localObject == null)
          {
            localObject = new HashMap();
            paramAnonymousMap1.put(paramAnonymousTransaction.getSenderId(), localObject);
          }
          Long localLong = (Long)((Map)localObject).get(localColoredCoinsAssetTransfer.getAssetId());
          if (localLong == null) {
            localLong = Long.valueOf(0L);
          }
          ((Map)localObject).put(localColoredCoinsAssetTransfer.getAssetId(), Long.valueOf(localLong.longValue() + localColoredCoinsAssetTransfer.getQuantity()));
        }
        
        void validateAttachment(Transaction paramAnonymousTransaction)
          throws NxtException.ValidationException
        {
          if (Blockchain.getLastBlock().getHeight() < 111111) {
            throw new Transaction.NotYetEnabledException("Asset Exchange not yet enabled at height " + Blockchain.getLastBlock().getHeight());
          }
          Attachment.ColoredCoinsAssetTransfer localColoredCoinsAssetTransfer = (Attachment.ColoredCoinsAssetTransfer)paramAnonymousTransaction.attachment;
          if ((paramAnonymousTransaction.amount != 0) || (localColoredCoinsAssetTransfer.getQuantity() <= 0) || (localColoredCoinsAssetTransfer.getQuantity() > 1000000000L)) {
            throw new NxtException.ValidationException("Invalid asset quantity or transaction amount");
          }
        }
      };
      
      static abstract class ColoredCoinsOrderPlacement
        extends Transaction.Type.ColoredCoins
      {
        abstract Attachment.ColoredCoinsOrderPlacement makeAttachment(Long paramLong, int paramInt, long paramLong1);
        
        final void loadAttachment(Transaction paramTransaction, ByteBuffer paramByteBuffer)
          throws NxtException.ValidationException
        {
          Long localLong = Convert.zeroToNull(paramByteBuffer.getLong());
          int i = paramByteBuffer.getInt();
          long l = paramByteBuffer.getLong();
          paramTransaction.attachment = makeAttachment(localLong, i, l);
          validateAttachment(paramTransaction);
        }
        
        final void loadAttachment(Transaction paramTransaction, JSONObject paramJSONObject)
          throws NxtException.ValidationException
        {
          Long localLong = Convert.parseUnsignedLong((String)paramJSONObject.get("asset"));
          int i = ((Long)paramJSONObject.get("quantity")).intValue();
          long l = ((Long)paramJSONObject.get("price")).longValue();
          paramTransaction.attachment = makeAttachment(localLong, i, l);
          validateAttachment(paramTransaction);
        }
        
        final void validateAttachment(Transaction paramTransaction)
          throws NxtException.ValidationException
        {
          if (Blockchain.getLastBlock().getHeight() < 111111) {
            throw new Transaction.NotYetEnabledException("Asset Exchange not yet enabled at height " + Blockchain.getLastBlock().getHeight());
          }
          Attachment.ColoredCoinsOrderPlacement localColoredCoinsOrderPlacement = (Attachment.ColoredCoinsOrderPlacement)paramTransaction.attachment;
          if ((!Genesis.CREATOR_ID.equals(paramTransaction.recipientId)) || (paramTransaction.amount != 0) || (localColoredCoinsOrderPlacement.getQuantity() <= 0) || (localColoredCoinsOrderPlacement.getQuantity() > 1000000000L) || (localColoredCoinsOrderPlacement.getPrice() <= 0L) || (localColoredCoinsOrderPlacement.getPrice() > 100000000000L)) {
            throw new NxtException.ValidationException("Invalid order quantity or price");
          }
        }
      }
      
      public static final Transaction.Type ASK_ORDER_PLACEMENT = new ColoredCoinsOrderPlacement()
      {
        public final byte getSubtype()
        {
          return 2;
        }
        
        final Attachment.ColoredCoinsOrderPlacement makeAttachment(Long paramAnonymousLong, int paramAnonymousInt, long paramAnonymousLong1)
        {
          return new Attachment.ColoredCoinsAskOrderPlacement(paramAnonymousLong, paramAnonymousInt, paramAnonymousLong1);
        }
        
        boolean checkDoubleSpending(Transaction paramAnonymousTransaction, Account paramAnonymousAccount, int paramAnonymousInt)
        {
          Attachment.ColoredCoinsAskOrderPlacement localColoredCoinsAskOrderPlacement = (Attachment.ColoredCoinsAskOrderPlacement)paramAnonymousTransaction.attachment;
          Integer localInteger = paramAnonymousAccount.getUnconfirmedAssetBalance(localColoredCoinsAskOrderPlacement.getAssetId());
          if ((localInteger == null) || (localInteger.intValue() < localColoredCoinsAskOrderPlacement.getQuantity()))
          {
            paramAnonymousAccount.addToUnconfirmedBalance(paramAnonymousInt * 100L);
            return true;
          }
          paramAnonymousAccount.addToUnconfirmedAssetBalance(localColoredCoinsAskOrderPlacement.getAssetId(), -localColoredCoinsAskOrderPlacement.getQuantity());
          return false;
        }
        
        void apply(Transaction paramAnonymousTransaction, Account paramAnonymousAccount1, Account paramAnonymousAccount2)
        {
          Attachment.ColoredCoinsAskOrderPlacement localColoredCoinsAskOrderPlacement = (Attachment.ColoredCoinsAskOrderPlacement)paramAnonymousTransaction.attachment;
          if (Asset.getAsset(localColoredCoinsAskOrderPlacement.getAssetId()) != null) {
            Order.Ask.addOrder(paramAnonymousTransaction.getId(), paramAnonymousAccount1, localColoredCoinsAskOrderPlacement.getAssetId(), localColoredCoinsAskOrderPlacement.getQuantity(), localColoredCoinsAskOrderPlacement.getPrice());
          }
        }
        
        void undo(Transaction paramAnonymousTransaction, Account paramAnonymousAccount1, Account paramAnonymousAccount2)
          throws Transaction.UndoNotSupportedException
        {
          Attachment.ColoredCoinsAskOrderPlacement localColoredCoinsAskOrderPlacement = (Attachment.ColoredCoinsAskOrderPlacement)paramAnonymousTransaction.attachment;
          Order.Ask localAsk = Order.Ask.removeOrder(paramAnonymousTransaction.getId());
          if ((localAsk == null) || (localAsk.getQuantity() != localColoredCoinsAskOrderPlacement.getQuantity()) || (!localAsk.getAssetId().equals(localColoredCoinsAskOrderPlacement.getAssetId()))) {
            throw new Transaction.UndoNotSupportedException(paramAnonymousTransaction, "Ask order already filled");
          }
          paramAnonymousAccount1.addToAssetAndUnconfirmedAssetBalance(localColoredCoinsAskOrderPlacement.getAssetId(), localColoredCoinsAskOrderPlacement.getQuantity());
        }
        
        void updateTotals(Transaction paramAnonymousTransaction, Map<Long, Long> paramAnonymousMap, Map<Long, Map<Long, Long>> paramAnonymousMap1, Long paramAnonymousLong)
        {
          Attachment.ColoredCoinsAskOrderPlacement localColoredCoinsAskOrderPlacement = (Attachment.ColoredCoinsAskOrderPlacement)paramAnonymousTransaction.attachment;
          Object localObject = (Map)paramAnonymousMap1.get(paramAnonymousTransaction.getSenderId());
          if (localObject == null)
          {
            localObject = new HashMap();
            paramAnonymousMap1.put(paramAnonymousTransaction.getSenderId(), localObject);
          }
          Long localLong = (Long)((Map)localObject).get(localColoredCoinsAskOrderPlacement.getAssetId());
          if (localLong == null) {
            localLong = Long.valueOf(0L);
          }
          ((Map)localObject).put(localColoredCoinsAskOrderPlacement.getAssetId(), Long.valueOf(localLong.longValue() + localColoredCoinsAskOrderPlacement.getQuantity()));
        }
      };
      public static final Transaction.Type BID_ORDER_PLACEMENT = new ColoredCoinsOrderPlacement()
      {
        public final byte getSubtype()
        {
          return 3;
        }
        
        final Attachment.ColoredCoinsOrderPlacement makeAttachment(Long paramAnonymousLong, int paramAnonymousInt, long paramAnonymousLong1)
        {
          return new Attachment.ColoredCoinsBidOrderPlacement(paramAnonymousLong, paramAnonymousInt, paramAnonymousLong1);
        }
        
        boolean checkDoubleSpending(Transaction paramAnonymousTransaction, Account paramAnonymousAccount, int paramAnonymousInt)
        {
          Attachment.ColoredCoinsBidOrderPlacement localColoredCoinsBidOrderPlacement = (Attachment.ColoredCoinsBidOrderPlacement)paramAnonymousTransaction.attachment;
          if (paramAnonymousAccount.getUnconfirmedBalance() < localColoredCoinsBidOrderPlacement.getQuantity() * localColoredCoinsBidOrderPlacement.getPrice())
          {
            paramAnonymousAccount.addToUnconfirmedBalance(paramAnonymousInt * 100L);
            return true;
          }
          paramAnonymousAccount.addToUnconfirmedBalance(-localColoredCoinsBidOrderPlacement.getQuantity() * localColoredCoinsBidOrderPlacement.getPrice());
          return false;
        }
        
        void apply(Transaction paramAnonymousTransaction, Account paramAnonymousAccount1, Account paramAnonymousAccount2)
        {
          Attachment.ColoredCoinsBidOrderPlacement localColoredCoinsBidOrderPlacement = (Attachment.ColoredCoinsBidOrderPlacement)paramAnonymousTransaction.attachment;
          if (Asset.getAsset(localColoredCoinsBidOrderPlacement.getAssetId()) != null) {
            Order.Bid.addOrder(paramAnonymousTransaction.getId(), paramAnonymousAccount1, localColoredCoinsBidOrderPlacement.getAssetId(), localColoredCoinsBidOrderPlacement.getQuantity(), localColoredCoinsBidOrderPlacement.getPrice());
          }
        }
        
        void undo(Transaction paramAnonymousTransaction, Account paramAnonymousAccount1, Account paramAnonymousAccount2)
          throws Transaction.UndoNotSupportedException
        {
          Attachment.ColoredCoinsBidOrderPlacement localColoredCoinsBidOrderPlacement = (Attachment.ColoredCoinsBidOrderPlacement)paramAnonymousTransaction.attachment;
          Order.Bid localBid = Order.Bid.removeOrder(paramAnonymousTransaction.getId());
          if ((localBid == null) || (localBid.getQuantity() != localColoredCoinsBidOrderPlacement.getQuantity()) || (!localBid.getAssetId().equals(localColoredCoinsBidOrderPlacement.getAssetId()))) {
            throw new Transaction.UndoNotSupportedException(paramAnonymousTransaction, "Bid order already filled");
          }
          paramAnonymousAccount1.addToBalanceAndUnconfirmedBalance(localColoredCoinsBidOrderPlacement.getQuantity() * localColoredCoinsBidOrderPlacement.getPrice());
        }
        
        void updateTotals(Transaction paramAnonymousTransaction, Map<Long, Long> paramAnonymousMap, Map<Long, Map<Long, Long>> paramAnonymousMap1, Long paramAnonymousLong)
        {
          Attachment.ColoredCoinsBidOrderPlacement localColoredCoinsBidOrderPlacement = (Attachment.ColoredCoinsBidOrderPlacement)paramAnonymousTransaction.attachment;
          paramAnonymousMap.put(paramAnonymousTransaction.getSenderId(), Long.valueOf(paramAnonymousLong.longValue() + localColoredCoinsBidOrderPlacement.getQuantity() * localColoredCoinsBidOrderPlacement.getPrice()));
        }
      };
      
      static abstract class ColoredCoinsOrderCancellation
        extends Transaction.Type.ColoredCoins
      {
        final void validateAttachment(Transaction paramTransaction)
          throws NxtException.ValidationException
        {
          if (Blockchain.getLastBlock().getHeight() < 111111) {
            throw new Transaction.NotYetEnabledException("Asset Exchange not yet enabled at height " + Blockchain.getLastBlock().getHeight());
          }
          if ((!Genesis.CREATOR_ID.equals(paramTransaction.recipientId)) || (paramTransaction.amount != 0)) {
            throw new NxtException.ValidationException("Invalid transaction amount or recipient");
          }
        }
        
        final boolean checkDoubleSpending(Transaction paramTransaction, Account paramAccount, int paramInt)
        {
          return false;
        }
        
        final void updateTotals(Transaction paramTransaction, Map<Long, Long> paramMap, Map<Long, Map<Long, Long>> paramMap1, Long paramLong) {}
        
        final void undo(Transaction paramTransaction, Account paramAccount1, Account paramAccount2)
          throws Transaction.UndoNotSupportedException
        {
          throw new Transaction.UndoNotSupportedException(paramTransaction, "Reversal of order cancellation not supported");
        }
      }
      
      public static final Transaction.Type ASK_ORDER_CANCELLATION = new ColoredCoinsOrderCancellation()
      {
        public final byte getSubtype()
        {
          return 4;
        }
        
        void loadAttachment(Transaction paramAnonymousTransaction, ByteBuffer paramAnonymousByteBuffer)
          throws NxtException.ValidationException
        {
          paramAnonymousTransaction.attachment = new Attachment.ColoredCoinsAskOrderCancellation(Convert.zeroToNull(paramAnonymousByteBuffer.getLong()));
          validateAttachment(paramAnonymousTransaction);
        }
        
        void loadAttachment(Transaction paramAnonymousTransaction, JSONObject paramAnonymousJSONObject)
          throws NxtException.ValidationException
        {
          paramAnonymousTransaction.attachment = new Attachment.ColoredCoinsAskOrderCancellation(Convert.parseUnsignedLong((String)paramAnonymousJSONObject.get("order")));
          validateAttachment(paramAnonymousTransaction);
        }
        
        void apply(Transaction paramAnonymousTransaction, Account paramAnonymousAccount1, Account paramAnonymousAccount2)
        {
          Attachment.ColoredCoinsAskOrderCancellation localColoredCoinsAskOrderCancellation = (Attachment.ColoredCoinsAskOrderCancellation)paramAnonymousTransaction.attachment;
          Order.Ask localAsk = Order.Ask.removeOrder(localColoredCoinsAskOrderCancellation.getOrderId());
          if (localAsk != null) {
            paramAnonymousAccount1.addToAssetAndUnconfirmedAssetBalance(localAsk.getAssetId(), localAsk.getQuantity());
          }
        }
      };
      public static final Transaction.Type BID_ORDER_CANCELLATION = new ColoredCoinsOrderCancellation()
      {
        public final byte getSubtype()
        {
          return 5;
        }
        
        void loadAttachment(Transaction paramAnonymousTransaction, ByteBuffer paramAnonymousByteBuffer)
          throws NxtException.ValidationException
        {
          paramAnonymousTransaction.attachment = new Attachment.ColoredCoinsBidOrderCancellation(Convert.zeroToNull(paramAnonymousByteBuffer.getLong()));
          validateAttachment(paramAnonymousTransaction);
        }
        
        void loadAttachment(Transaction paramAnonymousTransaction, JSONObject paramAnonymousJSONObject)
          throws NxtException.ValidationException
        {
          paramAnonymousTransaction.attachment = new Attachment.ColoredCoinsBidOrderCancellation(Convert.parseUnsignedLong((String)paramAnonymousJSONObject.get("order")));
          validateAttachment(paramAnonymousTransaction);
        }
        
        void apply(Transaction paramAnonymousTransaction, Account paramAnonymousAccount1, Account paramAnonymousAccount2)
        {
          Attachment.ColoredCoinsBidOrderCancellation localColoredCoinsBidOrderCancellation = (Attachment.ColoredCoinsBidOrderCancellation)paramAnonymousTransaction.attachment;
          Order.Bid localBid = Order.Bid.removeOrder(localColoredCoinsBidOrderCancellation.getOrderId());
          if (localBid != null) {
            paramAnonymousAccount1.addToBalanceAndUnconfirmedBalance(localBid.getQuantity() * localBid.getPrice());
          }
        }
      };
    }
  }
  
  public static final class UndoNotSupportedException
    extends NxtException
  {
    private final Transaction transaction;
    
    public UndoNotSupportedException(Transaction paramTransaction, String paramString)
    {
      super();
      this.transaction = paramTransaction;
    }
    
    public Transaction getTransaction()
    {
      return this.transaction;
    }
  }
  
  public static final class NotYetEnabledException
    extends NxtException.ValidationException
  {
    public NotYetEnabledException(String paramString)
    {
      super();
    }
    
    public NotYetEnabledException(String paramString, Throwable paramThrowable)
    {
      super(paramThrowable);
    }
  }
}