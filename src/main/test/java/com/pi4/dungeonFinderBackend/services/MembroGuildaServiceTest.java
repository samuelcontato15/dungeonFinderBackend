package com.pi4.dungeonFinderBackend.services;

import com.pi4.dungeonFinderBackend.datasource.repositories.UsuarioRepository;
import com.pi4.dungeonFinderBackend.domain.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class MembroGuildaServiceTest {

    @Autowired
    private MembroGuildaService membroGuildaService;

    @Autowired
    private GuildaService guildaService;

    @Autowired
    private JogoService jogoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private UUID jogoId;
    private UUID adminId;
    private UUID liderId;
    private UUID membroId;
    private UUID foraId;
    private UUID guildaId;

    @BeforeEach
    void setUp() {
        adminId = usuarioRepository.findByEmail("admin@admin.com")
                .orElseThrow(() -> new RuntimeException("Admin padrão não encontrado"))
                .getId();

        Jogo jogo = new Jogo();
        jogo.setNome("Jogo Membro");
        jogo.setSlug("jogo-membro");
        jogo.setCapa("");
        jogo.setCategoria(CategoriaJogo.MMO);
        jogoId = jogoService.criar(jogo, adminId).getId();

        Usuario lider = new Usuario();
        lider.setNick("lider_membro");
        lider.setEmail("lider@membro.com");
        lider.setSenhaHash("123");
        liderId = usuarioService.criar(lider).getId();

        Usuario membro = new Usuario();
        membro.setNick("membro_guilda");
        membro.setEmail("membro@guilda.com");
        membro.setSenhaHash("123");
        membroId = usuarioService.criar(membro).getId();

        Usuario fora = new Usuario();
        fora.setNick("fora_guilda");
        fora.setEmail("fora@guilda.com");
        fora.setSenhaHash("123");
        foraId = usuarioService.criar(fora).getId();

        Guilda guilda = new Guilda();
        guilda.setNome("Guilda Membro");
        guilda.setDescricao("Desc");
        guilda.setBanner("");
        guilda.setMaxMembros(5);
        guildaId = guildaService.criar(guilda, jogoId, liderId).getId();
    }

    @Test
    void entrar_comSucesso() {
        MembroGuilda m = membroGuildaService.entrar(guildaId, membroId);
        assertThat(m.getId()).isNotNull();
        assertThat(m.getPapel()).isEqualTo(PapelGuilda.MEMBRO);
    }

    @Test
    void entrar_duplicado_lancaExcecao() {
        // lider já é membro
        assertThatThrownBy(() -> membroGuildaService.entrar(guildaId, liderId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Usuário já é membro desta guilda");
    }

    @Test
    void entrar_guildaCheia_lancaExcecao() {
        // max = 5, lider já ocupa 1; adiciona mais 4 para lotar
        for (int i = 0; i < 4; i++) {
            Usuario u = new Usuario();
            u.setNick("lotacao_" + i);
            u.setEmail("lotacao" + i + "@guilda.com");
            u.setSenhaHash("123");
            UUID uid = usuarioService.criar(u).getId();
            membroGuildaService.entrar(guildaId, uid);
        }
        assertThatThrownBy(() -> membroGuildaService.entrar(guildaId, foraId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Guilda está cheia");
    }

    @Test
    void listarMembros_retornaMembros() {
        membroGuildaService.entrar(guildaId, membroId);
        List<MembroGuilda> membros = membroGuildaService.listarMembros(guildaId);
        assertThat(membros).hasSizeGreaterThanOrEqualTo(2); // lider + membro
    }

    @Test
    void alterarPapel_comSucesso() {
        membroGuildaService.entrar(guildaId, membroId);
        MembroGuilda atualizado = membroGuildaService.alterarPapel(guildaId, membroId, PapelGuilda.OFICIAL, liderId);
        assertThat(atualizado.getPapel()).isEqualTo(PapelGuilda.OFICIAL);
    }

    @Test
    void alterarPapel_paraLider_lancaExcecao() {
        membroGuildaService.entrar(guildaId, membroId);
        assertThatThrownBy(() -> membroGuildaService.alterarPapel(guildaId, membroId, PapelGuilda.LIDER, liderId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Não é possível transferir a liderança por aqui");
    }

    @Test
    void sair_comSucesso() {
        membroGuildaService.entrar(guildaId, membroId);
        membroGuildaService.sair(guildaId, membroId);
        List<MembroGuilda> membros = membroGuildaService.listarMembros(guildaId);
        assertThat(membros.stream().noneMatch(m -> m.getUsuario().getId().equals(membroId))).isTrue();
    }

    @Test
    void sair_lider_lancaExcecao() {
        assertThatThrownBy(() -> membroGuildaService.sair(guildaId, liderId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("O líder não pode sair da guilda");
    }

    @Test
    void sair_naoMembro_lancaExcecao() {
        assertThatThrownBy(() -> membroGuildaService.sair(guildaId, foraId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Usuário não é membro desta guilda");
    }

    @Test
    void expulsar_comSucesso() {
        membroGuildaService.entrar(guildaId, membroId);
        membroGuildaService.expulsar(guildaId, membroId, liderId);
        List<MembroGuilda> membros = membroGuildaService.listarMembros(guildaId);
        assertThat(membros.stream().noneMatch(m -> m.getUsuario().getId().equals(membroId))).isTrue();
    }

    @Test
    void expulsar_lider_lancaExcecao() {
        assertThatThrownBy(() -> membroGuildaService.expulsar(guildaId, liderId, adminId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Não é possível expulsar o líder");
    }

    @Test
    void expulsar_porMembro_lancaExcecao() {
        membroGuildaService.entrar(guildaId, membroId);
        assertThatThrownBy(() -> membroGuildaService.expulsar(guildaId, liderId, membroId))
                .isInstanceOf(ResponseStatusException.class);
    }
}