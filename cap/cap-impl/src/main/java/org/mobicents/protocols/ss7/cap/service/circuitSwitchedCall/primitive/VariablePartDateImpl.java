/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.mobicents.protocols.ss7.cap.service.circuitSwitchedCall.primitive;

import java.io.IOException;

import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.mobicents.protocols.ss7.cap.api.CAPException;
import org.mobicents.protocols.ss7.cap.api.CAPParsingComponentException;
import org.mobicents.protocols.ss7.cap.api.CAPParsingComponentExceptionReason;
import org.mobicents.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.VariablePartDate;
import org.mobicents.protocols.ss7.cap.primitives.CAPAsnPrimitive;

/**
 *
 * @author sergey vetyutnev
 *
 */
public class VariablePartDateImpl implements VariablePartDate, CAPAsnPrimitive {

    public static final String _PrimitiveName = "VariablePartDate";

    private byte[] data;

    public VariablePartDateImpl() {
    }

    public VariablePartDateImpl(byte[] data) {
        this.data = data;
    }

    public VariablePartDateImpl(int year, int month, int day) {
        this.data = new byte[4];

        this.data[0] = (byte) this.encodeByte(year / 100);
        this.data[1] = (byte) this.encodeByte(year % 100);
        this.data[2] = (byte) this.encodeByte(month);
        this.data[3] = (byte) this.encodeByte(day);
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    public int getYear() {

        if (this.data == null || this.data.length != 4)
            return 0;

        return this.decodeByte(data[0]) * 100 + this.decodeByte(data[1]);
    }

    @Override
    public int getMonth() {

        if (this.data == null || this.data.length != 4)
            return 0;

        return this.decodeByte(data[2]);
    }

    @Override
    public int getDay() {

        if (this.data == null || this.data.length != 4)
            return 0;

        return this.decodeByte(data[3]);
    }

    private int decodeByte(int bt) {
        return (bt & 0x0F) * 10 + ((bt & 0xF0) >> 4);
    }

    private int encodeByte(int val) {
        return (val / 10) | (val % 10) << 4;
    }

    @Override
    public int getTag() throws CAPException {
        return Tag.STRING_OCTET;
    }

    @Override
    public int getTagClass() {
        return Tag.CLASS_UNIVERSAL;
    }

    @Override
    public boolean getIsPrimitive() {
        return true;
    }

    @Override
    public void decodeAll(AsnInputStream ansIS) throws CAPParsingComponentException {

        try {
            int length = ansIS.readLength();
            this._decode(ansIS, length);
        } catch (IOException e) {
            throw new CAPParsingComponentException("IOException when decoding " + _PrimitiveName + ": " + e.getMessage(), e,
                    CAPParsingComponentExceptionReason.MistypedParameter);
        } catch (AsnException e) {
            throw new CAPParsingComponentException("AsnException when decoding " + _PrimitiveName + ": " + e.getMessage(), e,
                    CAPParsingComponentExceptionReason.MistypedParameter);
        }
    }

    @Override
    public void decodeData(AsnInputStream ansIS, int length) throws CAPParsingComponentException {

        try {
            this._decode(ansIS, length);
        } catch (IOException e) {
            throw new CAPParsingComponentException("IOException when decoding " + _PrimitiveName + ": " + e.getMessage(), e,
                    CAPParsingComponentExceptionReason.MistypedParameter);
        } catch (AsnException e) {
            throw new CAPParsingComponentException("AsnException when decoding " + _PrimitiveName + ": " + e.getMessage(), e,
                    CAPParsingComponentExceptionReason.MistypedParameter);
        }
    }

    private void _decode(AsnInputStream ansIS, int length) throws CAPParsingComponentException, IOException, AsnException {

        this.data = ansIS.readOctetStringData(length);
        if (this.data.length < 4 || this.data.length > 4)
            throw new CAPParsingComponentException("Error while decoding " + _PrimitiveName
                    + ": data must be from 4 to 4 bytes length, found: " + this.data.length,
                    CAPParsingComponentExceptionReason.MistypedParameter);
    }

    @Override
    public void encodeAll(AsnOutputStream asnOs) throws CAPException {
        this.encodeAll(asnOs, this.getTagClass(), this.getTag());
    }

    @Override
    public void encodeAll(AsnOutputStream asnOs, int tagClass, int tag) throws CAPException {

        try {
            asnOs.writeTag(tagClass, this.getIsPrimitive(), tag);
            int pos = asnOs.StartContentDefiniteLength();
            this.encodeData(asnOs);
            asnOs.FinalizeContent(pos);
        } catch (AsnException e) {
            throw new CAPException("AsnException when encoding " + _PrimitiveName + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void encodeData(AsnOutputStream asnOs) throws CAPException {

        if (this.data == null)
            throw new CAPException("Error while encoding " + _PrimitiveName + ": data field must not be null");
        if (this.data.length != 4)
            throw new CAPException("Error while encoding " + _PrimitiveName + ": data field length must be equal 4");

        asnOs.writeOctetStringData(data);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append(_PrimitiveName);
        sb.append(" [");

        if (this.data != null && this.data.length == 4) {
            sb.append("year=");
            sb.append(this.getYear());
            sb.append(", month=");
            sb.append(this.getMonth());
            sb.append(", day=");
            sb.append(this.getDay());
        }

        sb.append("]");

        return sb.toString();
    }
}
