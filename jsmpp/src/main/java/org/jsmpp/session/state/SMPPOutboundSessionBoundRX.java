/*
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jsmpp.session.state;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import org.jsmpp.bean.Command;
import org.jsmpp.bean.DeliverSmResp;
import org.jsmpp.extra.PendingResponse;
import org.jsmpp.extra.SessionState;
import org.jsmpp.session.OutboundResponseHandler;
import org.jsmpp.util.DefaultDecomposer;
import org.jsmpp.util.PDUDecomposer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is bound_rx state implementation of {@link SMPPOutboundSessionState}.
 * Response to receiver related transaction.
 * 
 * @author uudashr
 * @version 1.0
 * @since 2.3
 * 
 */
class SMPPOutboundSessionBoundRX extends SMPPOutboundSessionBound implements SMPPOutboundSessionState {
    private static final Logger logger = LoggerFactory.getLogger(SMPPOutboundSessionBoundRX.class);
    private static final PDUDecomposer pduDecomposer = new DefaultDecomposer();
    
    public SessionState getSessionState() {
        return SessionState.BOUND_RX;
    }

    public void processDeliverSmResp(Command pduHeader, byte[] pdu,
                                     OutboundResponseHandler responseHandler) {
        processDeliverSmResp0(pduHeader, pdu, responseHandler);
    }

    static final void processDeliverSmResp0(Command pduHeader, byte[] pdu,
                                            OutboundResponseHandler responseHandler) {
        CompletableFuture<Command> commandCompletableFuture = responseHandler.removeSentItemAsync(pduHeader.getSequenceNumber());
        if (commandCompletableFuture != null) {
            DeliverSmResp resp = pduDecomposer.deliverSmResp(pdu);
            commandCompletableFuture.complete(resp);
        } else {
            logger.warn("No request with sequence_number {} found", pduHeader.getSequenceNumber());
        }
    }
}
