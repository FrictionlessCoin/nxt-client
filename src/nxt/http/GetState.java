package nxt.http;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import nxt.Account;
import nxt.Alias;
import nxt.Asset;
import nxt.Block;
import nxt.Blockchain;
import nxt.Generator;
import nxt.Order.Ask;
import nxt.Order.Bid;
import nxt.Poll;
import nxt.Trade;
import nxt.Vote;
import nxt.peer.Peer;
import nxt.user.User;
import nxt.util.Convert;
import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;

public final class GetState
  extends HttpRequestDispatcher.HttpRequestHandler
{
  static final GetState instance = new GetState();
  
  JSONStreamAware processRequest(HttpServletRequest paramHttpServletRequest)
  {
    JSONObject localJSONObject = new JSONObject();
    
    localJSONObject.put("version", "0.7.6");
    localJSONObject.put("time", Integer.valueOf(Convert.getEpochTime()));
    localJSONObject.put("lastBlock", Blockchain.getLastBlock().getStringId());
    localJSONObject.put("cumulativeDifficulty", Blockchain.getLastBlock().getCumulativeDifficulty().toString());
    
    long l1 = 0L;
    for (Iterator localIterator = Account.getAllAccounts().iterator(); localIterator.hasNext();)
    {
      localObject = (Account)localIterator.next();
      long l2 = ((Account)localObject).getEffectiveBalance();
      if (l2 > 0L) {
        l1 += l2;
      }
    }
    localJSONObject.put("totalEffectiveBalance", Long.valueOf(l1 * 100L));
    
    localJSONObject.put("numberOfBlocks", Integer.valueOf(Blockchain.getBlockCount()));
    localJSONObject.put("numberOfTransactions", Integer.valueOf(Blockchain.getTransactionCount()));
    localJSONObject.put("numberOfAccounts", Integer.valueOf(Account.getAllAccounts().size()));
    localJSONObject.put("numberOfAssets", Integer.valueOf(Asset.getAllAssets().size()));
    localJSONObject.put("numberOfOrders", Integer.valueOf(Order.Ask.getAllAskOrders().size() + Order.Bid.getAllBidOrders().size()));
    int i = 0;
    for (Object localObject = Trade.getAllTrades().iterator(); ((Iterator)localObject).hasNext();)
    {
      List localList = (List)((Iterator)localObject).next();
      i += localList.size();
    }
    localJSONObject.put("numberOfTrades", Integer.valueOf(i));
    localJSONObject.put("numberOfAliases", Integer.valueOf(Alias.getAllAliases().size()));
    localJSONObject.put("numberOfPolls", Integer.valueOf(Poll.getAllPolls().size()));
    localJSONObject.put("numberOfVotes", Integer.valueOf(Vote.getVotes().size()));
    localJSONObject.put("numberOfPeers", Integer.valueOf(Peer.getAllPeers().size()));
    localJSONObject.put("numberOfUsers", Integer.valueOf(User.getAllUsers().size()));
    localJSONObject.put("numberOfUnlockedAccounts", Integer.valueOf(Generator.getAllGenerators().size()));
    localObject = Blockchain.getLastBlockchainFeeder();
    localJSONObject.put("lastBlockchainFeeder", localObject == null ? null : ((Peer)localObject).getAnnouncedAddress());
    localJSONObject.put("availableProcessors", Integer.valueOf(Runtime.getRuntime().availableProcessors()));
    localJSONObject.put("maxMemory", Long.valueOf(Runtime.getRuntime().maxMemory()));
    localJSONObject.put("totalMemory", Long.valueOf(Runtime.getRuntime().totalMemory()));
    localJSONObject.put("freeMemory", Long.valueOf(Runtime.getRuntime().freeMemory()));
    
    return localJSONObject;
  }
}