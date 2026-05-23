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
class GuildaServiceTest {

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
    private UUID usuarioComumId;

    @BeforeEach
    void setUp() {
        adminId = usuarioRepository.findByEmail("admin@admin.com")
                .orElseThrow(() -> new RuntimeException("Admin padrão não encontrado"))
                .getId();

        Jogo jogo = new Jogo();
        jogo.setNome("Jogo Guilda");
        jogo.setSlug("jogo-guilda");
        jogo.setCapa("");
        jogo.setCategoria(CategoriaJogo.MMO);
        jogoId = jogoService.criar(jogo, adminId).getId();

        Usuario lider = new Usuario();
        lider.setNick("lider_guilda");
        lider.setEmail("lider@guilda.com");
        lider.setSenhaHash("123");
        liderId = usuarioService.criar(lider).getId();

        Usuario comum = new Usuario();
        comum.setNick("comum_guilda");
        comum.setEmail("comum@guilda.com");
        comum.setSenhaHash("123");
        usuarioComumId = usuarioService.criar(comum).getId();
    }

    private Guilda criarGuildaBase(String nome, UUID criadorId) {
        Guilda guilda = new Guilda();
        guilda.setNome(nome);
        guilda.setDescricao("Descrição");
        guilda.setBanner("");
        guilda.setMaxMembros(10);
        return guildaService.criar(guilda, jogoId, criadorId);
    }

    @Test
    void criar_comSucesso() {
        Guilda guilda = criarGuildaBase("Guilda Teste", liderId);
        assertThat(guilda.getId()).isNotNull();
        assertThat(guilda.getNome()).isEqualTo("Guilda Teste");
        assertThat(guilda.getCriadoPor().getId()).isEqualTo(liderId);
    }

    @Test
    void criar_nomeDuplicado_lancaExcecao() {
        criarGuildaBase("Guilda Unica", liderId);
        Guilda duplicada = new Guilda();
        duplicada.setNome("Guilda Unica");
        duplicada.setMaxMembros(10);
        assertThatThrownBy(() -> guildaService.criar(duplicada, jogoId, liderId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Nome já cadastrado");
    }

    @Test
    void buscarPorId_comSucesso() {
        Guilda criada = criarGuildaBase("Guilda BuscaId", liderId);
        Guilda encontrada = guildaService.buscarPorId(criada.getId());
        assertThat(encontrada.getNome()).isEqualTo("Guilda BuscaId");
    }

    @Test
    void buscarPorId_inexistente_lancaExcecao() {
        assertThatThrownBy(() -> guildaService.buscarPorId(UUID.randomUUID()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Guilda não encontrada");
    }

    @Test
    void listarPorJogo_retornaGuildasDoJogo() {
        criarGuildaBase("Guilda Jogo", liderId);
        List<Guilda> guildas = guildaService.listarPorJogo(jogoId);
        assertThat(guildas).hasSize(1);
        assertThat(guildas.get(0).getJogo().getId()).isEqualTo(jogoId);
    }

    @Test
    void deletar_peloLider_comSucesso() {
        Guilda guilda = criarGuildaBase("Guilda Deletar", liderId);
        UUID id = guilda.getId();
        guildaService.deletar(id, liderId);
        assertThatThrownBy(() -> guildaService.buscarPorId(id))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Guilda não encontrada");
    }

    @Test
    void deletar_peloAdmin_comSucesso() {
        Guilda guilda = criarGuildaBase("Guilda Admin Delete", liderId);
        UUID id = guilda.getId();
        guildaService.deletar(id, adminId);
        assertThatThrownBy(() -> guildaService.buscarPorId(id))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Guilda não encontrada");
    }

    @Test
    void deletar_porUsuarioSemPermissao_lancaExcecao() {
        Guilda guilda = criarGuildaBase("Guilda Proibida", liderId);
        assertThatThrownBy(() -> guildaService.deletar(guilda.getId(), usuarioComumId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Apenas o líder ou admin pode deletar a guilda");
    }
}