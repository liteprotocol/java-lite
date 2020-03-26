package org.tron.core.services.http;

import com.alibaba.fastjson.JSONObject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tron.core.Wallet;
import org.tron.protos.Protocol.Account;


@Component
@Slf4j(topic = "API")
public class GetAccountByIdServlet extends RateLimiterServlet {

  @Autowired
  private Wallet wallet;

  protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    try {
      boolean visible = Util.getVisible(request);
      String accountId = request.getParameter("account_id");
      Account.Builder build = Account.newBuilder();
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("account_id", accountId);
      JsonFormat.merge(jsonObject.toJSONString(), build, visible);

      Account reply = wallet.getAccountById(build.build());
      Util.printAccount(reply, response, visible);
    } catch (Exception e) {
      Util.processError(e, response);
    }
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    try {
      PostParams params = PostParams.getPostParams(request);
      Account.Builder build = Account.newBuilder();
      JsonFormat.merge(params.getParams(), build, params.isVisible());

      Account reply = wallet.getAccountById(build.build());
      Util.printAccount(reply, response, params.isVisible());
    } catch (Exception e) {
      Util.processError(e, response);
    }
  }
}