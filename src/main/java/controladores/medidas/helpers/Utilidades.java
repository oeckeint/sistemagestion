package controladores.medidas.helpers;

import datos.entity.medidas.MedidaQH;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Utilidades {

    public static void crearCsvMedidasQH(HttpServletResponse response, @NotNull List<MedidaQH> medidasQH) throws IOException {

        StringBuilder csvContent = new StringBuilder();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //Cabecera de MedidaQH para el csv
        csvContent.append("id_medidaQH").append(",");
        csvContent.append("id_cliente").append(",");
        csvContent.append("tipo medida").append(",");
        csvContent.append("fecha").append(",");
        csvContent.append("bandera_inv_ver").append(",");
        csvContent.append("actent").append(",");
        csvContent.append("qactent").append(",");
        csvContent.append("actsal").append(",");
        csvContent.append("qactsal").append(",");
        csvContent.append("r_q1").append(",");
        csvContent.append("qr_q1").append(",");
        csvContent.append("r_q2").append(",");
        csvContent.append("qr_q2").append(",");
        csvContent.append("r_q3").append(",");
        csvContent.append("qr_q3").append(",");
        csvContent.append("r_q4").append(",");
        csvContent.append("qr_q4").append(",");
        csvContent.append("medres1").append(",");
        csvContent.append("qmedres1").append(",");
        csvContent.append("medres2").append(",");
        csvContent.append("qmedres2").append(",");
        csvContent.append("metod_obt").append(",");
        csvContent.append("created_on").append(",");
        csvContent.append("created_by").append(",");
        csvContent.append("updated_on").append(",");
        csvContent.append("updated_by").append(",");
        csvContent.append("temporal").append("\n");

        for (MedidaQH medidaQH : medidasQH) {
            csvContent.append(medidaQH.getId()).append(",");
            csvContent.append(medidaQH.getCliente().getIdCliente()).append(",");
            csvContent.append(medidaQH.getTipoMed()).append(",");
            csvContent.append(formatter.format(medidaQH.getFecha())).append(",");
            csvContent.append(medidaQH.getBanderaInvVer()).append(",");
            csvContent.append(medidaQH.getActent()).append(",");
            csvContent.append(medidaQH.getQactent()).append(",");
            csvContent.append(medidaQH.getActsal()).append(",");
            csvContent.append(medidaQH.getQactsal()).append(",");
            csvContent.append(medidaQH.getR_q1()).append(",");
            csvContent.append(medidaQH.getQr_q1()).append(",");
            csvContent.append(medidaQH.getR_q2()).append(",");
            csvContent.append(medidaQH.getQr_q2()).append(",");
            csvContent.append(medidaQH.getR_q3()).append(",");
            csvContent.append(medidaQH.getQr_q3()).append(",");
            csvContent.append(medidaQH.getR_q4()).append(",");
            csvContent.append(medidaQH.getQr_q4()).append(",");
            csvContent.append(medidaQH.getMedres1()).append(",");
            csvContent.append(medidaQH.getQmedres1()).append(",");
            csvContent.append(medidaQH.getMedres2()).append(",");
            csvContent.append(medidaQH.getQmedres2()).append(",");
            csvContent.append(medidaQH.getMetodObt()).append(",");
            csvContent.append(medidaQH.getCreatedOn() != null ? formatter.format(medidaQH.getCreatedOn().getTime()) : null).append(",");
            csvContent.append(medidaQH.getCreatedBy()).append(",");
            csvContent.append(medidaQH.getUpdatedOn() != null ? formatter.format(medidaQH.getUpdatedOn().getTime()) : null).append(",");
            csvContent.append(medidaQH.getUpdatedBy()).append(",");
            csvContent.append(medidaQH.getTemporal()).append("\n");
        }

        long idCliente = medidasQH.get(0).getCliente().getIdCliente();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatterFile = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");
        String formattedNow = now.format(formatterFile);

        String filename = "MedidaQH_" + idCliente + "_" + formattedNow + ".csv";

        response.setContentType(MediaType.TEXT_PLAIN_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
        response.getWriter().write(csvContent.toString());

    }

}
