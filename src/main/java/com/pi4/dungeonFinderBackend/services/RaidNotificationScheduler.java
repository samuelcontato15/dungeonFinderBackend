package com.pi4.dungeonFinderBackend.services;

import com.pi4.dungeonFinderBackend.datasource.repositories.ParticipanteRaidRepository;
import com.pi4.dungeonFinderBackend.datasource.repositories.RaidRepository;
import com.pi4.dungeonFinderBackend.domain.entities.ParticipanteRaid;
import com.pi4.dungeonFinderBackend.domain.entities.Raid;
import com.pi4.dungeonFinderBackend.domain.entities.TipoNotificacao;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RaidNotificationScheduler {

    private final RaidRepository raidRepository;
    private final ParticipanteRaidRepository participanteRaidRepository;
    private final NotificacaoService notificacaoService;

    @Scheduled(fixedRate = 60000)
    public void verificarRaids() {

        System.out.println("=== VERIFICANDO RAIDS ===");

        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime proximoMinuto = agora.plusMinutes(1);

        List<Raid> raids = raidRepository.findAll();

        for (Raid raid : raids) {

            if (raid.getInicioEm() == null) {
                continue;
            }

            if (Boolean.TRUE.equals(raid.getNotificacaoEnviada())) {
                continue;
            }

            boolean vaiComecar =
                    raid.getInicioEm().isAfter(agora)
                            && raid.getInicioEm().isBefore(proximoMinuto);

            if (vaiComecar) {

                System.out.println(
                        "Enviando notificações da raid: "
                                + raid.getNome()
                );

                List<ParticipanteRaid> participantes =
                        participanteRaidRepository
                                .findByRaidIdWithUsuario(
                                        raid.getId()
                                );

                for (ParticipanteRaid participante : participantes) {

                    notificacaoService.criar(
                            participante.getUsuario().getId(),
                            TipoNotificacao.NOVA_RAID,
                            "A raid '" + raid.getNome() + "' começa agora!",
                            raid.getId(),
                            "RAID"
                    );
                }

                raid.setNotificacaoEnviada(true);

                raidRepository.save(raid);
            }
        }
    }
}