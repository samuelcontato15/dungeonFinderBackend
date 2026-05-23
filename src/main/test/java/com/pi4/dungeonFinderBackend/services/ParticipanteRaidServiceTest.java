package com.pi4.dungeonFinderBackend.services;

import com.pi4.dungeonFinderBackend.datasource.repositories.UsuarioRepository;
import com.pi4.dungeonFinderBackend.domain.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class ParticipanteRaidServiceTest {

    @Autowired
    private ParticipanteRaidService participanteRaidService;

    @Autowired
    private RaidService raidService;

    @Autowired
    private JogoService jogoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private UUID jogoId;
    private UUID adminId;
    private UUID criadorId;
    private UUID outroUsuarioId;
    private UUID raidId;

    @BeforeEach
    void setUp() {
        // Admin
        adminId = usuarioRepository.findByEmail("admin@admin.com")
                .orElseThrow(() -> new RuntimeException("Admin padrão não encontrado"))
                .getId();

        // Jogo
        Jogo jogo = new Jogo();
        jogo.setNome("Jogo Participante");
        jogo.setSlug("jogo-participante");
        jogo.setCapa("");
        jogo.setCategoria(CategoriaJogo.MMO);
        jogoId = jogoService.criar(jogo, adminId).getId();

        // Criador da raid
        Usuario criador = new Usuario();
        criador.setNick("criador_raid");
        criador.setEmail("criador@raid.com");
        criador.setSenhaHash("123");
        criadorId = usuarioService.criar(criador).getId();

        // Outro usuário
        Usuario outro = new Usuario();
        outro.setNick("outro_raid");
        outro.setEmail("outro@raid.com");
        outro.setSenhaHash("123");
        outroUsuarioId = usuarioService.criar(outro).getId();

        // Raid — criador já entra automaticamente
        Raid raid = new Raid();
        raid.setNome("Raid Participante");
        raid.setDescricao("Descrição");
        raid.setMinJogadores(1);
        raid.setMaxJogadores(3);
        raid.setInicioEm(LocalDateTime.now().plusDays(1));
        raidId = raidService.criar(raid, jogoId, criadorId).getId();
    }

    @Test
    void inscrever_comSucesso() {
        ParticipanteRaid participante = participanteRaidService.inscrever(raidId, outroUsuarioId);
        assertThat(participante).isNotNull();
        assertThat(participante.getUsuario().getId()).isEqualTo(outroUsuarioId);
    }

    @Test
    void inscrever_duplicado_lancaExcecao() {
        // criador já está inscrito desde o criar
        assertThatThrownBy(() -> participanteRaidService.inscrever(raidId, criadorId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Usuário já inscrito nesta raid");
    }

    @Test
    void inscrever_raidLotada_lancaExcecao() {
        // max = 3, criador já ocupa 1 vaga; cria mais 2 usuários para lotar
        for (int i = 0; i < 2; i++) {
            Usuario u = new Usuario();
            u.setNick("lotacao_" + i);
            u.setEmail("lotacao" + i + "@raid.com");
            u.setSenhaHash("123");
            UUID uid = usuarioService.criar(u).getId();
            participanteRaidService.inscrever(raidId, uid);
        }
        assertThatThrownBy(() -> participanteRaidService.inscrever(raidId, outroUsuarioId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Raid já está lotada");
    }

    @Test
    void listarPorRaid_retornaParticipantes() {
        participanteRaidService.inscrever(raidId, outroUsuarioId);
        List<ParticipanteRaid> lista = participanteRaidService.listarPorRaid(raidId);
        assertThat(lista).hasSizeGreaterThanOrEqualTo(2); // criador + outroUsuario
    }

    @Test
    void sair_comSucesso() {
        participanteRaidService.inscrever(raidId, outroUsuarioId);
        participanteRaidService.sair(raidId, outroUsuarioId);
        List<ParticipanteRaid> lista = participanteRaidService.listarPorRaid(raidId);
        assertThat(lista.stream().noneMatch(p -> p.getUsuario().getId().equals(outroUsuarioId))).isTrue();
    }

    @Test
    void sair_semEstarInscrito_lancaExcecao() {
        assertThatThrownBy(() -> participanteRaidService.sair(raidId, outroUsuarioId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Usuário não está inscrito nesta raid");
    }
}