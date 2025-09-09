package com.mehrdad.tickets.mappers;

import com.mehrdad.tickets.domain.CreateEventRequest;
import com.mehrdad.tickets.domain.CreateTicketTypeRequest;
import com.mehrdad.tickets.domain.UpdateEventRequest;
import com.mehrdad.tickets.domain.UpdateTicketTypeRequest;
import com.mehrdad.tickets.domain.dtos.CreateEventRequestDto;
import com.mehrdad.tickets.domain.dtos.CreateEventResponseDto;
import com.mehrdad.tickets.domain.dtos.CreateTicketTypeRequestDto;
import com.mehrdad.tickets.domain.dtos.GetEventDetailsResponseDto;
import com.mehrdad.tickets.domain.dtos.GetEventDetailsTicketTypesResponseDto;
import com.mehrdad.tickets.domain.dtos.GetPublishedEventDetailsResponseDto;
import com.mehrdad.tickets.domain.dtos.GetPublishedEventDetailsTicketTypesResponseDto;
import com.mehrdad.tickets.domain.dtos.ListEventResponseDto;
import com.mehrdad.tickets.domain.dtos.ListEventTicketTypeResponseDto;
import com.mehrdad.tickets.domain.dtos.ListPublishedEventResponseDto;
import com.mehrdad.tickets.domain.dtos.UpdateEventRequestDto;
import com.mehrdad.tickets.domain.dtos.UpdateEventResponseDto;
import com.mehrdad.tickets.domain.dtos.UpdateTicketTypeRequestDto;
import com.mehrdad.tickets.domain.dtos.UpdateTicketTypeResponseDto;
import com.mehrdad.tickets.domain.entities.Event;
import com.mehrdad.tickets.domain.entities.TicketType;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {

  CreateTicketTypeRequest fromDto(CreateTicketTypeRequestDto dto);

  CreateEventRequest fromDto(CreateEventRequestDto dto);

  CreateEventResponseDto toDto(Event event);

  ListEventTicketTypeResponseDto toDto(TicketType ticketType);

  ListEventResponseDto toListEventResponseDto(Event event);

  GetEventDetailsTicketTypesResponseDto toGetEventDetailsTicketTypesResponseDto(
      TicketType ticketType);

  GetEventDetailsResponseDto toGetEventDetailsResponseDto(Event event);

  UpdateTicketTypeRequest fromDto(UpdateTicketTypeRequestDto dto);

  UpdateEventRequest fromDto(UpdateEventRequestDto dto);

  UpdateTicketTypeResponseDto toUpdateTicketTypeResponseDto(TicketType ticketType);

  UpdateEventResponseDto toUpdateEventResponseDto(Event event);

  ListPublishedEventResponseDto toListPublishedEventResponseDto(Event event);

  GetPublishedEventDetailsTicketTypesResponseDto toGetPublishedEventDetailsTicketTypesResponseDto(
      TicketType ticketType);

  GetPublishedEventDetailsResponseDto toGetPublishedEventDetailsResponseDto(Event event);
}
