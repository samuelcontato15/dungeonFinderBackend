package com.pi4.dungeonFinderBackend.services;

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
class EventoServiceTest {

    @Autowired
    private EventoService eventoService;

    @Autowired
    private JogoService jogoService;

    @Autowired
    private GuildaService guildaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private com.pi4.dungeonFinderBackend.datasource.repositories.UsuarioRepository usuarioRepository;

    private UUID jogoId;
    private UUID guildaId;
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
        jogo.setNome("Jogo Evento");
        jogo.setSlug("jogo-evento");
        jogo.setCapa("");
        jogo.setCategoria(CategoriaJogo.MMO);
        jogoId = jogoService.criar(jogo, adminId).getId();

        // Usuário comum
        Usuario comum = new Usuario();
        comum.setNick("comum_evento");
        comum.setEmail("comum@evento.com");
        comum.setSenhaHash("123");
        usuarioComumId = usuarioService.criar(comum).getId();

        // Guilda
        Guilda guilda = new Guilda();
        guilda.setNome("Guilda Evento");
        guilda.setMaxMembros(20);
        guildaId = guildaService.criar(guilda, jogoId, adminId).getId();
    }

    private Evento criarEventoBase() {
        Evento evento = new Evento();
        evento.setNome("Evento Teste");
        evento.setDescricao("Descrição do evento");
        evento.setBanner("");
        evento.setInicioEm(LocalDateTime.now().plusDays(1));
        evento.setFimEm(LocalDateTime.now().plusDays(2));
        return eventoService.criar(evento, jogoId, guildaId, adminId);
    }

    @Test
    void criarEvento_comSucesso() {
        Evento evento = criarEventoBase();
        assertThat(evento.getId()).isNotNull();
        assertThat(evento.getNome()).isEqualTo("Evento Teste");
    }

    @Test
    void criarEvento_nomeDuplicado_lancaExcecao() {
        criarEventoBase();
        Evento duplicado = new Evento();
        duplicado.setNome("Evento Teste");
        duplicado.setInicioEm(LocalDateTime.now().plusDays(1));
        duplicado.setFimEm(LocalDateTime.now().plusDays(2));
        assertThatThrownBy(() -> eventoService.criar(duplicado, jogoId, guildaId, adminId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Nome já cadastrado");
    }

    @Test
    void criarEvento_usuarioNaoAdmin_lancaExcecao() {
        Evento evento = new Evento();
        evento.setNome("Evento proibido");
        evento.setInicioEm(LocalDateTime.now().plusDays(1));
        evento.setFimEm(LocalDateTime.now().plusDays(2));
        assertThatThrownBy(() -> eventoService.criar(evento, jogoId, guildaId, usuarioComumId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Acesso negado");
    }

    @Test
    void buscarPorId_comSucesso() {
        Evento criado = criarEventoBase();
        Evento encontrado = eventoService.buscarPorId(criado.getId());
        assertThat(encontrado.getNome()).isEqualTo(criado.getNome());
    }

    @Test
    void listarPorJogo_retornaEventosDoJogo() {
        criarEventoBase();
        List<Evento> eventos = eventoService.listarPorJogo(jogoId);
        assertThat(eventos).hasSize(1);
        assertThat(eventos.get(0).getJogo().getId()).isEqualTo(jogoId);
    }

    @Test
    void atualizarEvento_comSucesso() {
        Evento evento = criarEventoBase();
        Evento novosDados = new Evento();
        novosDados.setNome("Evento Atualizado");
        novosDados.setDescricao("Nova descrição");
        novosDados.setBanner("novo_banner.jpg");
        novosDados.setInicioEm(LocalDateTime.now().plusDays(3));
        novosDados.setFimEm(LocalDateTime.now().plusDays(4));

        Evento atualizado = eventoService.atualizar(evento.getId(), novosDados, adminId);
        assertThat(atualizado.getNome()).isEqualTo("Evento Atualizado");
        assertThat(atualizado.getDescricao()).isEqualTo("Nova descrição");
    }

    @Test
    void deletarEvento_comSucesso() {
        Evento evento = criarEventoBase();
        UUID id = evento.getId();
        assertThat(id).isNotNull();
        eventoService.deletar(id, adminId);
        assertThatThrownBy(() -> eventoService.buscarPorId(id))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Evento não encontrado");
    }

    @Test
    void deletarEvento_porNaoAdmin_lancaExcecao() {
        Evento evento = criarEventoBase();
        assertThatThrownBy(() -> eventoService.deletar(evento.getId(), usuarioComumId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Acesso negado");
    }
}