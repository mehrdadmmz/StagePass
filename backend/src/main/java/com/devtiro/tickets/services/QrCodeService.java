package com.mehrdad.tickets.services;

import com.mehrdad.tickets.domain.entities.QrCode;
import com.mehrdad.tickets.domain.entities.Ticket;
import java.util.UUID;

public interface QrCodeService {

  QrCode generateQrCode(Ticket ticket);

  byte[] getQrCodeImageForUserAndTicket(UUID userId, UUID ticketId);
}
