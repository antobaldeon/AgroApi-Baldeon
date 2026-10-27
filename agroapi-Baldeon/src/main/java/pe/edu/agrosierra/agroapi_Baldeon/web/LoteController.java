package pe.edu.agrosierra.agroapi_Baldeon.web;

import pe.edu.agrosierra.agroapi_Baldeon.dominio.Lote;
import pe.edu.agrosierra.agroapi_Baldeon.servicio.LoteService;
import pe.edu.agrosierra.agroapi_Baldeon.seguridad.UsuarioActualService;
import pe.edu.agrosierra.agroapi_Baldeon.web.dto.LoteResponse;
import pe.edu.agrosierra.agroapi_Baldeon.web.dto.PaginaResponse;
import pe.edu.agrosierra.agroapi_Baldeon.web.dto.UsuarioActualResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class LoteController {

    private final LoteService loteService;
    private final UsuarioActualService usuarioActualService;

    public LoteController(LoteService loteService, UsuarioActualService usuarioActualService) {
        this.loteService = loteService;
        this.usuarioActualService = usuarioActualService;
    }

    @GetMapping("/lotes")
    public PaginaResponse<LoteResponse> listarLotes(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanio,
            @RequestParam(required = false) String valle,
            @RequestParam(required = false) Boolean campaniaCerrada) {
        Page<Lote> resultado = loteService.listar(pagina, tamanio, valle, campaniaCerrada);
        return PaginaResponse.desde(resultado, LoteResponse::desde);
    }

    @GetMapping("/lotes/{id}")
    public LoteResponse obtenerLote(@PathVariable Long id) {
        return LoteResponse.desde(loteService.obtener(id));
    }

    @GetMapping("/usuarios/me")
    public ResponseEntity<UsuarioActualResponse> usuarioActual(Authentication authentication) {
        return ResponseEntity.ok(usuarioActualService.obtener(authentication.getName()));
    }
}
