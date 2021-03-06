/* Copyright (c) 2011 Danish Maritime Authority.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dk.dma.epd.common.prototype.enavcloud;

import dk.dma.enav.model.voct.DatumPointDTO;
import dk.dma.enav.model.voct.DatumPointSARISDTO;
import dk.dma.enav.model.voct.EffortAllocationDTO;
import dk.dma.enav.model.voct.RapidResponseDTO;
import dk.dma.enav.model.voyage.Route;
import dk.dma.epd.common.prototype.model.voct.SAR_TYPE;
import dk.dma.epd.common.prototype.voct.VOCTManagerCommon.VoctMsgStatus;

import java.io.Serializable;
import java.util.Date;

import static java.util.Objects.requireNonNull;

public class VOCTCommunicationService {

    /** An initiation point */
    public static final TODO.ServiceInitiationPoint<VOCTCommunicationMessage> INIT = new TODO.ServiceInitiationPoint<>(
            VOCTCommunicationMessage.class);

    public static class VOCTCommunicationMessage extends TODO.ServiceMessage<VOCTCommunicationReply> implements Serializable {

        private static final long serialVersionUID = -3556815314608410502L;
        private RapidResponseDTO sarDataRapidResponse;
        private DatumPointDTO sarDataDatumPoint;
        private DatumPointSARISDTO sarDataDatumPointSaris;

        private EffortAllocationDTO effortAllocationData;
        private Route searchPattern;

        private Date sent;
        private String sender;
        private String message;
        private long id;
        private VoctMsgStatus status;
        private long receiversMMSI;
        private SAR_TYPE type;

        public VOCTCommunicationMessage() {
        }

        public VOCTCommunicationMessage(RapidResponseDTO sarData, EffortAllocationDTO effortAllocationData, Route searchPattern,
                String sender, String message, long id, long receiversMMSI) {
            super();
            this.sarDataRapidResponse = requireNonNull(sarData);
            this.effortAllocationData = effortAllocationData;
            this.searchPattern = searchPattern;
            this.sent = new Date();
            this.message = requireNonNull(message);
            this.sender = requireNonNull(sender);
            this.id = requireNonNull(id);
            this.receiversMMSI = receiversMMSI;
            type = SAR_TYPE.RAPID_RESPONSE;

        }

        public VOCTCommunicationMessage(DatumPointDTO sarData, EffortAllocationDTO effortAllocationData, Route searchPattern,
                String sender, String message, long id, long receiversMMSI) {
            super();
            this.sarDataDatumPoint = requireNonNull(sarData);
            this.effortAllocationData = effortAllocationData;
            this.searchPattern = searchPattern;
            this.sent = new Date();
            this.message = requireNonNull(message);
            this.sender = requireNonNull(sender);
            this.id = requireNonNull(id);
            this.receiversMMSI = receiversMMSI;
            type = SAR_TYPE.DATUM_POINT;
        }

        public VOCTCommunicationMessage(DatumPointSARISDTO sarData, EffortAllocationDTO effortAllocationData, Route searchPattern,
                String sender, String message, long id, long receiversMMSI) {
            super();
            this.sarDataDatumPointSaris = requireNonNull(sarData);
            this.effortAllocationData = effortAllocationData;
            this.searchPattern = searchPattern;
            this.sent = new Date();
            this.message = requireNonNull(message);
            this.sender = requireNonNull(sender);
            this.id = requireNonNull(id);
            this.receiversMMSI = receiversMMSI;
            type = SAR_TYPE.SARIS_DATUM_POINT;
        }

        /**
         * Constructor - used for replys
         */
        public VOCTCommunicationMessage(long id, String message, VoctMsgStatus status) {
            this.id = id;
            this.message = message;
            this.status = requireNonNull(status);
        }

        /**
         * Constructor - used to withdraw from an operation
         */
        public VOCTCommunicationMessage(long id, VoctMsgStatus status) {
            this.id = id;
            this.status = requireNonNull(status);
        }

        /**
         * @return the sarDataRapidResponse
         */
        public RapidResponseDTO getSarDataRapidResponse() {
            return sarDataRapidResponse;
        }

        /**
         * @param sarDataRapidResponse
         *            the sarDataRapidResponse to set
         */
        public void setSarDataRapidResponse(RapidResponseDTO sarDataRapidResponse) {
            this.sarDataRapidResponse = sarDataRapidResponse;
        }

        /**
         * @return the sarDataDatumPoint
         */
        public DatumPointDTO getSarDataDatumPoint() {
            return sarDataDatumPoint;
        }

        /**
         * @return the sarDataDatumPointSaris
         */
        public DatumPointSARISDTO getSarDataDatumPointSaris() {
            return sarDataDatumPointSaris;
        }

        /**
         * @param sarDataDatumPointSaris
         *            the sarDataDatumPointSaris to set
         */
        public void setSarDataDatumPointSaris(DatumPointSARISDTO sarDataDatumPointSaris) {
            this.sarDataDatumPointSaris = sarDataDatumPointSaris;
        }

        /**
         * @param sarDataDatumPoint
         *            the sarDataDatumPoint to set
         */
        public void setSarDataDatumPoint(DatumPointDTO sarDataDatumPoint) {
            this.sarDataDatumPoint = sarDataDatumPoint;
        }

        /**
         * @return the effortAllocationData
         */
        public EffortAllocationDTO getEffortAllocationData() {
            return effortAllocationData;
        }

        /**
         * @param effortAllocationData
         *            the effortAllocationData to set
         */
        public void setEffortAllocationData(EffortAllocationDTO effortAllocationData) {
            this.effortAllocationData = effortAllocationData;
        }

        /**
         * @return the searchPattern
         */
        public Route getSearchPattern() {
            return searchPattern;
        }

        /**
         * @param searchPattern
         *            the searchPattern to set
         */
        public void setSearchPattern(Route searchPattern) {
            this.searchPattern = searchPattern;
        }

        /**
         * @return the sent
         */
        public Date getSent() {
            return sent;
        }

        /**
         * @param sent
         *            the sent to set
         */
        public void setSent(Date sent) {
            this.sent = sent;
        }

        /**
         * @return the sender
         */
        public String getSender() {
            return sender;
        }

        /**
         * @param sender
         *            the sender to set
         */
        public void setSender(String sender) {
            this.sender = sender;
        }

        /**
         * @return the message
         */
        public String getMessage() {
            return message;
        }

        /**
         * @param message
         *            the message to set
         */
        public void setMessage(String message) {
            this.message = message;
        }

        /**
         * @return the id
         */
        public long getId() {
            return id;
        }

        /**
         * @param id
         *            the id to set
         */
        public void setId(long id) {
            this.id = id;
        }

        /**
         * @return the status
         */
        public VoctMsgStatus getStatus() {
            return status;
        }

        /**
         * @param status
         *            the status to set
         */
        public void setStatus(VoctMsgStatus status) {
            this.status = status;
        }

        /**
         * @return the receiversMMSI
         */
        public long getReceiversMMSI() {
            return receiversMMSI;
        }

        /**
         * @param receiversMMSI
         *            the receiversMMSI to set
         */
        public void setReceiversMMSI(long receiversMMSI) {
            this.receiversMMSI = receiversMMSI;
        }

        /**
         * @return the type
         */
        public SAR_TYPE getType() {
            return type;
        }

        /**
         * @param type
         *            the type to set
         */
        public void setType(SAR_TYPE type) {
            this.type = type;
        }

    }

    public static class VOCTCommunicationReply extends TODO.ServiceMessage<Void> {

        // private String message;
        private long id;
        private long mmsi;
        private long sendDate;

        public VOCTCommunicationReply() {
        }

        public VOCTCommunicationReply(long id, long mmsi, long sendDate) {
            // this.message = message;
            this.id = requireNonNull(id);
            this.mmsi = requireNonNull(mmsi);
            this.sendDate = requireNonNull(sendDate);
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public long getMmsi() {
            return mmsi;
        }

        public void setMmsi(long mmsi) {
            this.mmsi = mmsi;
        }

        public long getSendDate() {
            return sendDate;
        }

        public void setSendDate(long sendDate) {
            this.sendDate = sendDate;
        }

    }
}
