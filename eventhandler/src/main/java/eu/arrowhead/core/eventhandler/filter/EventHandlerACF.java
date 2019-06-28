/*
 * This work is part of the Productive 4.0 innovation project, which receives grants from the
 * European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 * (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 * national funding authorities from involved countries.
 */

package eu.arrowhead.core.eventhandler.filter;

import eu.arrowhead.common.Utility;
import eu.arrowhead.common.database.EventFilter;
import eu.arrowhead.common.exception.AuthException;
import eu.arrowhead.common.filter.AccessControlFilter;
import eu.arrowhead.common.messages.PublishEvent;
import eu.arrowhead.common.misc.SecurityUtils;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.AUTHORIZATION) //2nd highest priority constant, this filter gets executed after the SecurityFilter
public class EventHandlerACF extends AccessControlFilter {

  @Override
  public boolean isClientAuthorized(String clientCN, String method, String requestTarget, String requestJson) {
    if (!SecurityUtils.isKeyStoreCNArrowheadValid(clientCN)) {
      log.info(clientCN + " is not valid common name, access denied!");
      return false;
    }

    String serverCN = (String) configuration.getProperty("server_common_name");
    String[] serverFields = serverCN.split("\\.", 2);

    String[] clientFields = clientCN.split("\\.", 2);
    if (requestTarget.contains("publish")) {
      PublishEvent event = Utility.fromJson(requestJson, PublishEvent.class);
      if (!clientFields[0].equalsIgnoreCase(event.getSource().getSystemName())) {
        log.error("Source system name and cert common name do not match! Event publishing denied!");
        throw new AuthException("Source system " + event.getSource().getSystemName() + " and cert common name (" + clientCN + ") do not match!");
      }
    } else if (requestTarget.endsWith("subscription")) {
      EventFilter filter = Utility.fromJson(requestJson, EventFilter.class);
      if (!clientFields[0].equalsIgnoreCase(filter.getConsumer().getSystemName())) {
        log.error("Consumer system name and cert common name do not match! Event subscription/unsubscribe denied!");
        throw new AuthException("Consumer system " + filter.getConsumer().getSystemName() + " and cert common name (" + clientCN + ") do not match!");
      }
    } else {
      //Only the DELETE method based unsubscribe method left
      String[] uriParts = requestTarget.split("/");
      if (!clientFields[0].equalsIgnoreCase(uriParts[uriParts.length - 1])) {
        log.error("Consumer system name and cert common name do not match! Event unsubscribe denied!");
        throw new AuthException("Consumer system " + uriParts[uriParts.length - 1] + " and cert common name (" + clientCN + ") do not match!");
      }
    }

    // All requests from the local cloud are allowed
    return serverFields[1].equalsIgnoreCase(clientFields[1]);
  }

}
