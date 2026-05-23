package com.pi4.dungeonFinderBackend.services;

import com.pi4.dungeonFinderBackend.datasource.repositories.UsuarioRepository;
import com.pi4.dungeonFinderBackend.domain.entities.*;
import com.pi4.dungeonFinderBackend.dto.RaidRequest;
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
class RaidServiceTest {

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
    private UUID usuarioComumId;

    @BeforeEach
    void setUp() {
        // Admin — usa o admin criado pelo AdminInitializer
        adminId = usuarioRepository.findByEmail("admin@admin.com")
                .orElseThrow(() -> new RuntimeException("Admin padrão não encontrado"))
                .getId();

        // Jogo
        Jogo jogo = new Jogo();
        jogo.setNome("Jogo Raid");
        jogo.setSlug("jogo-raid");
        jogo.setCapa("");
        jogo.setCategoria(CategoriaJogo.MMO);
        jogoId = jogoService.criar(jogo, adminId).getId();

        // Usuário comum
        Usuario comum = new Usuario();
        comum.setNick("comum_raid");
        comum.setEmail("comum@raid.com");
        comum.setSenhaHash("123");
        usuarioComumId = usuarioService.criar(comum).getId();
    }

    private Raid criarRaidBase(UUID criadorId) {
        Raid raid = new Raid();
        raid.setNome("Raid Teste");
        raid.setDescricao("Descrição da raid");
        raid.setMinJogadores(1);
        raid.setMaxJogadores(5);
        raid.setInicioEm(LocalDateTime.now().plusDays(1));
        return raidService.criar(raid, jogoId, criadorId);
    }

    @Test
    void criarRaid_comSucesso() {
        Raid raid = criarRaidBase(usuarioComumId);
        assertThat(raid.getId()).isNotNull();
        assertThat(raid.getNome()).isEqualTo("Raid Teste");
        assertThat(raid.getCriadoPor().getId()).isEqualTo(usuarioComumId);
    }

    @Test
    void criarRaid_minJogadoresMenorQueUm_lancaExcecao() {
        Raid raid = new Raid();
        raid.setNome("Raid Invalida");
        raid.setMinJogadores(0);
        raid.setMaxJogadores(5);
        raid.setInicioEm(LocalDateTime.now().plusDays(1));
        assertThatThrownBy(() -> raidService.criar(raid, jogoId, usuarioComumId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("mínimo de jogadores deve ser pelo menos 1");
    }

    @Test
    void criarRaid_maxJogadoresMaiorQueDez_lancaExcecao() {
        Raid raid = new Raid();
        raid.setNome("Raid Invalida");
        raid.setMinJogadores(1);
        raid.setMaxJogadores(11);
        raid.setInicioEm(LocalDateTime.now().plusDays(1));
        assertThatThrownBy(() -> raidService.criar(raid, jogoId, usuarioComumId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("máximo de jogadores não pode ultrapassar 10");
    }

    @Test
    void criarRaid_maxMenorQueMin_lancaExcecao() {
        Raid raid = new Raid();
        raid.setNome("Raid Invalida");
        raid.setMinJogadores(5);
        raid.setMaxJogadores(3);
        raid.setInicioEm(LocalDateTime.now().plusDays(1));
        assertThatThrownBy(() -> raidService.criar(raid, jogoId, usuarioComumId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("máximo não pode ser menor que o mínimo");
    }

    @Test
    void buscarPorId_comSucesso() {
        Raid criada = criarRaidBase(usuarioComumId);
        Raid encontrada = raidService.buscarPorId(criada.getId());
        assertThat(encontrada.getNome()).isEqualTo("Raid Teste");
    }

    @Test
    void buscarPorId_naoEncontrado_lancaExcecao() {
        assertThatThrownBy(() -> raidService.buscarPorId(UUID.randomUUID()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Raid não encontrada");
    }

    @Test
    void listarPorJogo_retornaRaidsDoJogo() {
        criarRaidBase(usuarioComumId);
        List<Raid> raids = raidService.listarPorJogo(jogoId);
        assertThat(raids).hasSize(1);
        assertThat(raids.get(0).getJogo().getId()).isEqualTo(jogoId);
    }

    @Test
    void editarRaid_comSucesso() {
        Raid criada = criarRaidBase(usuarioComumId);
        RaidRequest request = new RaidRequest(
                "Raid Editada",
                "Nova descrição",
                2,
                8,
                LocalDateTime.now().plusDays(3)
        );
        Raid editada = raidService.editar(criada.getId(), request, usuarioComumId);
        assertThat(editada.getNome()).isEqualTo("Raid Editada");
        assertThat(editada.getMaxJogadores()).isEqualTo(8);
    }

    @Test
    void editarRaid_porOutroUsuario_lancaExcecao() {
        Raid criada = criarRaidBase(usuarioComumId);
        RaidRequest request = new RaidRequest(
                "Raid Editada",
                "Nova descrição",
                1,
                5,
                LocalDateTime.now().plusDays(3)
        );
        assertThatThrownBy(() -> raidService.editar(criada.getId(), request, adminId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Apenas o criador pode editar esta raid");
    }

    @Test
    void deletarRaid_comSucesso() {
        Raid criada = criarRaidBase(usuarioComumId);
        UUID id = criada.getId();
        raidService.deletar(id, usuarioComumId);
        assertThatThrownBy(() -> raidService.buscarPorId(id))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Raid não encontrada");
    }

    @Test
    void deletarRaid_porOutroUsuario_lancaExcecao() {
        Raid criada = criarRaidBase(usuarioComumId);
        assertThatThrownBy(() -> raidService.deletar(criada.getId(), adminId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Apenas o criador pode deletar esta raid");
    }
}