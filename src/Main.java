import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

class Mesa {
    private boolean ocupada;
    private long tiempoInicio;
    private int personasOcupadas;
    private int numeroMesa;
    private double precioPorMinuto;

    private int totalIngresosGenerados;
    private int totalPersonasOcupadas;
    private int vecesOcupada = 0;

    //constructur de la clase mesa
    public Mesa(int numeroMesa, double precioPorMinuto) {
        this.ocupada = false;
        this.tiempoInicio = 0;
        this.personasOcupadas = 0;
        this.numeroMesa = numeroMesa;
        this.precioPorMinuto = precioPorMinuto;
    }

    public int getVecesOcupada() {
        return vecesOcupada;
    }

    public boolean estaOcupada() {
        return ocupada;
    }

    public void ocupar(int personas) {
        if (!this.ocupada) { // Si la mesa no está ocupada, cambia su estado y registra el tiempo de inicio
            this.ocupada = true;
            this.personasOcupadas = personas;
            this.tiempoInicio = System.currentTimeMillis() / 60000; // Guardar el tiempo actual en minutos
            this.vecesOcupada++;
        }
    }
    private long tiempoAcumuladoOcupacion;

    // Método para actualizar el tiempo acumulado de ocupación al desocupar una mesa
    public void actualizarTiempoAcumulado() {
        if (estaOcupada()) {
            long tiempoFin = System.currentTimeMillis() / 60000;
            tiempoAcumuladoOcupacion += tiempoFin - tiempoInicio;
        }
    }

    // Método getter para obtener el tiempo acumulado de ocupación
    public long getTiempoAcumuladoOcupacion() {
        return tiempoAcumuladoOcupacion;
    }
    // Método para desocupar la mesa y calcular el costo
    public int desocupar(int personasARetirar, Map<Integer, Integer> dineroPorMesa) {
        if (personasARetirar >= personasOcupadas) {         // Desocupa la mesa y actualiza los registros de tiempo y dinero
            actualizarTiempoAcumulado();         // Actualiza el tiempo acumulado de ocupación si la mesa está ocupada
            this.ocupada = false;
            personasARetirar = personasOcupadas; // Retirar todas las personas
        } else {
            personasOcupadas -= personasARetirar;
        }

        long tiempoFin = System.currentTimeMillis() / 60000;
        long tiempoOcupada = tiempoFin - tiempoInicio;
        int costo = (int) (tiempoOcupada * personasARetirar * obtenerPrecioPorMinuto());
        System.out.println("Клиенты освободили столы " + personasARetirar + obtenerNumeroMesa() +
                ". Время за столом: " + tiempoOcupada + " минуты. Стоимость (чек): " + costo);
        dineroPorMesa.put(obtenerNumeroMesa(), dineroPorMesa.getOrDefault(obtenerNumeroMesa(), 0) + costo); // Actualiza el dinero por mesa

// Agregar el costo al total de ingresos generados y actualizar el número de personas
        totalIngresosGenerados += costo;
        totalPersonasOcupadas += personasARetirar;

        return obtenerNumeroMesa();
    }
    // Métodos getter para los nuevos atributos
    public int getTotalIngresosGenerados() {
        return totalIngresosGenerados;
    }
    public int getTotalPersonasOcupadas() {
        return totalPersonasOcupadas;
    }
    public int obtenerPersonasOcupadas() {
        return personasOcupadas;
    }
    public long obtenerTiempoInicio() {
        return tiempoInicio;
    }
    public int obtenerNumeroMesa() {
        return numeroMesa;
    }
    public double obtenerPrecioPorMinuto() {
        return precioPorMinuto;
    }

    public void establecerPrecioPorMinuto(double nuevoPrecio) {
        if (nuevoPrecio >= 0) {
            precioPorMinuto = nuevoPrecio;
        } else {
            System.out.println("Ошибка.");
        }
    }
}

// Clase que representa el anticafe, que gestiona las mesas
class Anticafe {
    private Map<Integer, Mesa> mesas;  // Mapa de mesas del anticafe
    public Map<Integer, Mesa> getMesas() {
        return mesas;
    }
    private Map<Integer, Integer> dineroPorMesa;  // Registro de dinero generado por cada mesa
    private double precioGlobalPorMinuto; // Precio global por minuto para todas las mesas
    private int totalPersonas; // Total de personas que han visitado el anticafe

    public Anticafe(double precioGlobalPorMinuto) {
        this.mesas = new HashMap<>();
        this.dineroPorMesa = new HashMap<>();
        this.precioGlobalPorMinuto = precioGlobalPorMinuto;
        this.totalPersonas = 0;
        for (int i = 1; i <= 10; i++) {
            mesas.put(i, new Mesa(i, precioGlobalPorMinuto));
        }
    }
    // Métodos para obtener información sobre las mesas y su ocupación
    public List<Integer> obtenerMesasDesocupadas() {
        List<Integer> mesasDesocupadas = new ArrayList<>();         // Devuelve una lista con los números de las mesas desocupadas
        for (Map.Entry<Integer, Mesa> entry : mesas.entrySet()) {
            int mesaNumero = entry.getKey();
            Mesa mesa = entry.getValue();
            if (!mesa.estaOcupada()) {
                mesasDesocupadas.add(mesaNumero);
            }
        }
        return mesasDesocupadas;
    }

    public void mostrarEstadisticasActuales() {
        StringBuilder estadisticas = new StringBuilder("Текущая статистика:\n");
        for (Map.Entry<Integer, Mesa> entry : mesas.entrySet()) {
            int mesaNumero = entry.getKey();
            Mesa mesa = entry.getValue();
            if (mesa.estaOcupada()) {
                estadisticas.append("Стол ").append(mesaNumero).append(": занят  ").append(mesa.obtenerPersonasOcupadas())
                        .append(" клиентами в течение ").append((System.currentTimeMillis() / 60000 - mesa.obtenerTiempoInicio()))
                        .append(" минут\n");
            } else {
                estadisticas.append("Стол ").append(mesaNumero).append(": Свободен\n");
            }
        }
        JOptionPane.showMessageDialog(null, estadisticas.toString());
    }

    public void mostrarEstadisticasFinales() {
        StringBuilder estadisticas = new StringBuilder("Полная статистика:\n");

        long tiempoTranscurrido = System.currentTimeMillis() / 60000; // Tiempo transcurrido en minutos

        for (int mesaNumero = 1; mesaNumero <= 10; mesaNumero++) {
            Mesa mesa = mesas.get(mesaNumero);
            int vecesOcupada = mesa.getVecesOcupada();

            estadisticas.append("Стол ").append(mesaNumero).append(": был занят ").append(vecesOcupada)
                    .append(" раз, время, в течение которлого стол был занят: ").append(mesa.getTiempoAcumuladoOcupacion())
                    .append(" минуты, Доход: ").append(mesa.getTotalIngresosGenerados()).append("\n");
        }

        JOptionPane.showMessageDialog(null, estadisticas.toString());
    }


    public void mostrarEstadisticaFinanciera() {
        StringBuilder estadisticas = new StringBuilder("Финансовая статистика стола:\n");
        int totalIngresos = 0; // Variable para almacenar el total de ingresos

        for (Map.Entry<Integer, Mesa> entry : mesas.entrySet()) {
            int mesaNumero = entry.getKey();
            Mesa mesa = entry.getValue();
            int ingresosMesa = mesa.getTotalIngresosGenerados();
            estadisticas.append("Стол ").append(mesaNumero)
                    .append(": Доход: ").append(ingresosMesa)
                    .append(", Общее количество клиентов: ").append(mesa.getTotalPersonasOcupadas()).append("\n");
            totalIngresos += ingresosMesa; // Sumar los ingresos de la mesa al total
        }

        estadisticas.append("\nСовокупный доход со всех столов: ").append(totalIngresos); // Añadir el total al final
        JOptionPane.showMessageDialog(null, estadisticas.toString());
    }


    public void mostrarTotalPersonas() {
        JOptionPane.showMessageDialog(null, "Совокупное количество клиентов, посетивших антикафе: " + totalPersonas);
    }

    public void ocuparMesa(int numeroMesa, int personas) {
        if (validarNumeroMesa(numeroMesa)) {
            Mesa mesa = mesas.get(numeroMesa);
            if (!mesa.estaOcupada()) {
                mesa.ocupar(personas);
                totalPersonas += personas; // Incrementa el total de personas al ocupar una mesa
                System.out.println("Стол " + numeroMesa + " занят " + personas + " клиетном.");
            } else {
                System.out.println("Стол " + numeroMesa + " уже занят.");
            }
        } else {
            System.out.println("Стол уже занят.");
        }
    }

    public int desocuparMesa(int numeroMesa, int personasARetirar) {
        if (validarNumeroMesa(numeroMesa)) {
            Mesa mesa = mesas.get(numeroMesa);
            if (mesa.estaOcupada()) {
                if (personasARetirar > 0) {
                    return mesa.desocupar(personasARetirar, dineroPorMesa);
                } else {
                    mesa.desocupar(mesa.obtenerPersonasOcupadas(), dineroPorMesa);
                    return 0; // No hubo retirada de personas
                }
            } else {
                System.out.println("Стол " + numeroMesa + " свободен.");
            }
        } else {
            System.out.println("Стол не занят.");
        }
        return 0;
    }

    // para la mesa frecuente
    public int mesaMasFrecuente() {
        int maxFrecuencia = 0;
        int mesaMasFrecuente = -1;
        for (Map.Entry<Integer, Mesa> entry : mesas.entrySet()) {
            Mesa mesa = entry.getValue();
            int vecesOcupada = mesa.getVecesOcupada();
            if (vecesOcupada > maxFrecuencia) {
                maxFrecuencia = vecesOcupada;
                mesaMasFrecuente = entry.getKey();
            }
        }
        return mesaMasFrecuente;
    }

    public void ajustarPrecioPorMesa(int numeroMesa, double nuevoPrecio) {
        if (validarNumeroMesa(numeroMesa)) {
            Mesa mesa = mesas.get(numeroMesa);
            if (mesa != null) {
                mesa.establecerPrecioPorMinuto(nuevoPrecio);
                System.out.println("Поминутная стоимость стола " + numeroMesa + " установленная: " + nuevoPrecio);
            } else {
                System.out.println("Стол не найден.");
            }
        } else {
            System.out.println("Стол уже занят.");
        }
    }

    public void establecerPrecioGlobalPorMinuto(double nuevoPrecio) {
        if (nuevoPrecio >= 0) {
            precioGlobalPorMinuto = nuevoPrecio;
            // Actualizar el precio por minuto de cada mesa
            for (Map.Entry<Integer, Mesa> entry : mesas.entrySet()) {
                Mesa mesa = entry.getValue();
                mesa.establecerPrecioPorMinuto(nuevoPrecio);
            }
            System.out.println("Полная стоимость " + nuevoPrecio + " за все столы.");
        } else {
            System.out.println("Полная стоимость.");
        }
    }

    public double obtenerPrecioGlobalPorMinuto() {
        return precioGlobalPorMinuto;
    }

    public Map<Integer, Integer> obtenerDineroPorMesa() {
        return dineroPorMesa;
    }

    public Mesa obtenerMesa(int numeroMesa) {
        return mesas.get(numeroMesa);
    }

    public List<Integer> obtenerMesasOcupadas() {
        List<Integer> mesasOcupadas = new ArrayList<>();
        for (Map.Entry<Integer, Mesa> entry : mesas.entrySet()) {
            int mesaNumero = entry.getKey();
            Mesa mesa = entry.getValue();
            if (mesa.estaOcupada()) {
                mesasOcupadas.add(mesaNumero);
            }
        }
        return mesasOcupadas;
    }

    public boolean validarNumeroMesa(int numeroMesa) {
        return numeroMesa >= 1 && numeroMesa <= 10;
    }
}

class AnticafeGUI extends JFrame {
    private Anticafe anticafe;
    private Map<Integer, JButton> mesaButtons;

    public AnticafeGUI() {
        anticafe = new Anticafe(1.0); // Precio global por minuto inicial
        mesaButtons = new HashMap<>();
        initComponents();
    }

    private void initComponents() {
        setTitle("Приложение Антикафе");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mesaButtonPanel = new JPanel(new GridLayout(2, 5));
        JPanel controlButtonPanel = new JPanel(new FlowLayout());

        // Crear y agregar botones de mesa al panel
        // Dentro del bucle que crea los botones de mesa en initComponents
        for (int i = 1; i <= 10; i++) {
            JButton mesaButton = new JButton("Стол " + i);
            mesaButtons.put(i, mesaButton);
            actualizarEstadoBoton(i); // Actualizar color del botón según la ocupación
            mesaButtonPanel.add(mesaButton);

            mesaButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JButton clickedButton = (JButton) e.getSource();
                    int numeroMesa = Integer.parseInt(clickedButton.getText().split(" ")[1]);

                    Mesa mesa = anticafe.obtenerMesa(numeroMesa);

                    if (mesa != null) {
                        if (mesa.estaOcupada()) {
                            int personasEnMesa = mesa.obtenerPersonasOcupadas();
                            String personasARetirarString = JOptionPane.showInputDialog(
                                    "Стол " + numeroMesa + " занят " + personasEnMesa + " клиентом.\n"
                                            + "Количество людей, желающих отменить бронь:"
                            );

                            try {
                                int personasARetirar = Integer.parseInt(personasARetirarString);
                                if (personasARetirar > 0 && personasARetirar <= personasEnMesa) {
                                    anticafe.desocuparMesa(numeroMesa, personasARetirar); // Desocupar la mesa
                                    actualizarEstadoBoton(numeroMesa); // Actualizar el color del botón
                                    JOptionPane.showMessageDialog(null,
                                            "Отменили " + personasARetirar + " бронь со стола " + numeroMesa
                                    );
                                } else {
                                    JOptionPane.showMessageDialog(null, "Пожалуйста, введите действительное количество клиентов, которые отменили бронь.");
                                }
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(null, "Пожалуйста, введите действительное количество клиентов, которые отменили бронь.");
                            }
                        } else {
                            gestionarOcupacionMesa(numeroMesa);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Номер стола не действителен.");
                    }
                }
            });
        }

// Crear y agregar botones de control al panel
        JButton estadisticasActualesButton = new JButton("Показать актуальную статистику");
        JButton estadisticasFinalesButton = new JButton("Показать финальную статистику");
        JButton estadisticaFinancieraButton = new JButton("Показать финансовую статистику");
        JButton totalPersonasButton = new JButton("Показать всех клиентов");
        JButton ajustarPrecioGlobalButton = new JButton("Настроить полную стоимость");
        JButton ajustarPrecioMesaButton = new JButton("Настроить стоимость стола");
        JButton abandonarMesaButton = new JButton("Удалить стол");
        JButton salirButton = new JButton("Выйти");
        JButton estadisticasPersonalizadasButton = new JButton("Персональная статистика");



        controlButtonPanel.add(estadisticasActualesButton);
        controlButtonPanel.add(estadisticasFinalesButton);
        controlButtonPanel.add(estadisticaFinancieraButton);
        controlButtonPanel.add(totalPersonasButton);
        controlButtonPanel.add(ajustarPrecioGlobalButton);
        controlButtonPanel.add(ajustarPrecioMesaButton);
        controlButtonPanel.add(abandonarMesaButton);
        controlButtonPanel.add(salirButton);
        controlButtonPanel.add(estadisticasPersonalizadasButton);


        // Agregar acción al botón de estadísticas actuales
        estadisticasActualesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                anticafe.mostrarEstadisticasActuales();
            }
        });

        // Agregar acción al botón de estadísticas finales
        estadisticasFinalesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                anticafe.mostrarEstadisticasFinales();
            }
        });

        // Agregar acción al botón de estadística financiera
        estadisticaFinancieraButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                anticafe.mostrarEstadisticaFinanciera();
            }
        });

        // Agregar acción al botón de mostrar total de personas
        totalPersonasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                anticafe.mostrarTotalPersonas();
            }
        });

        // Agregar acción al botón de ajustar precio global
        ajustarPrecioGlobalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nuevoPrecioString = JOptionPane.showInputDialog("Введите новую стоимость за минуту:");
                try {
                    double nuevoPrecio = Double.parseDouble(nuevoPrecioString);
                    anticafe.establecerPrecioGlobalPorMinuto(nuevoPrecio);
                    JOptionPane.showMessageDialog(null, "Полная стоимость настроена правильно.");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Пожалуйста, введите действительное число.");
                }
            }
        });

// Agregar acción al botón de ajustar precio de mesa
        ajustarPrecioMesaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String numeroMesaString = JOptionPane.showInputDialog("Введите номер стола:");
                try {
                    int numeroMesa = Integer.parseInt(numeroMesaString);
                    if (anticafe.validarNumeroMesa(numeroMesa)) {
                        String nuevoPrecioString = JOptionPane.showInputDialog("Введите новую стоимость стола за минуту " + numeroMesa + ":");
                        try {
                            double nuevoPrecio = Double.parseDouble(nuevoPrecioString);
                            anticafe.ajustarPrecioPorMesa(numeroMesa, nuevoPrecio);
                            JOptionPane.showMessageDialog(null, "Стоимость стола настроена правильно.");
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(null, "Пожалуйства, введите действительную цену для стола.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Номер стола не действителен.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Пожалуйста, введите действительный номер стола.");
                }
            }
        });

        // Agregar acción al botón de abandonar mesa
        abandonarMesaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String numeroMesaString = JOptionPane.showInputDialog("Введите номе стола:");
                try {
                    int numeroMesa = Integer.parseInt(numeroMesaString);
                    if (anticafe.validarNumeroMesa(numeroMesa)) {
                        Mesa mesa = anticafe.obtenerMesa(numeroMesa);
                        if (mesa != null && mesa.estaOcupada()) {
                            int personasEnMesa = mesa.obtenerPersonasOcupadas();
                            String mensaje = "За столом " + numeroMesa + " уже есть " + personasEnMesa + " гости.\n"
                                    + "Введите количество гостей, чтобы снять столик:";
                            String personasARetirarString = JOptionPane.showInputDialog(mensaje);
                            try {
                                int personasARetirar = Integer.parseInt(personasARetirarString);
                                if (personasARetirar > 0) {
                                    int mesaRetirada = anticafe.desocuparMesa(numeroMesa, personasARetirar);
                                    if (mesaRetirada > 0) {
                                        actualizarEstadoBoton(numeroMesa);
                                        JOptionPane.showMessageDialog(null, "Гости освобождают стол " + mesaRetirada);
                                    } else {
                                        JOptionPane.showMessageDialog(null, "Нет гостей, чтобы забронировать стол " + numeroMesa + ".");
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(null, "Пожалуйства, введите действительное еоличество гостей.");
                                }
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(null, "Пожалуйства, введите действительное количество гостей.");
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Стол " + numeroMesa + " не занят.");
                            // Aquí puedes agregar la llamada a actualizarEstadoBoton para que cambie a verde
                            actualizarEstadoBoton(numeroMesa);
                        }

                    } else {
                        JOptionPane.showMessageDialog(null, "Номер стола не действителен.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Пожалуйства, введите действительный номер стола.");
                }
            }
        });

        // Agregar acción al botón de salir
        salirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // Agregar acción al botón de estadísticas personalizadas
        estadisticasPersonalizadasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarEstadisticasPersonalizadas();
            }
        });

        // Agregar paneles al frame
        add(mesaButtonPanel, BorderLayout.CENTER);
        add(controlButtonPanel, BorderLayout.SOUTH);

        // Configurar la ventana
        pack();
        setLocationRelativeTo(null); // Centrar la ventana
        setVisible(true);
    }

    private void gestionarOcupacionMesa(int numeroMesa) {
        String personasString = JOptionPane.showInputDialog("Введите количетсво гостей:");
        try {
            int personas = Integer.parseInt(personasString);
            anticafe.ocuparMesa(numeroMesa, personas);
            actualizarEstadoBoton(numeroMesa);
            JOptionPane.showMessageDialog(null, "Стол " + numeroMesa + " занят " + personas + " гостями.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Пожалуйства, введите действительное количество гостей.");
        }
    }

    private void actualizarEstadoBoton(int numeroMesa) {
        Mesa mesa = anticafe.obtenerMesa(numeroMesa);
        JButton mesaButton = mesaButtons.get(numeroMesa);

        if (mesa != null && mesa.estaOcupada()) {
            mesaButton.setBackground(Color.RED); // Color rojo para mesas ocupadas
        } else {
            mesaButton.setBackground(Color.GREEN); // Color verde para mesas libres
        }
        // Repintar el botón para reflejar el cambio de color
        mesaButton.repaint();
    }
    private void mostrarEstadisticasPersonalizadas() {
        double tiempoPromedio = calcularTiempoPromedioOcupacion();
        int mesaMasFrecuente = encontrarMesaMasFrecuente();
        int mesaMasDineroGenerado = encontrarMesaMasDineroGenerado();

        StringBuilder estadisticas = new StringBuilder("Персональная статистика:\n");
        estadisticas.append("1) Среднее время за столом: ").append(tiempoPromedio).append(" минут\n");
        estadisticas.append("2) Какой стол Вы чаще всего выбираете: Стол ").append(mesaMasFrecuente).append("\n");

        if (mesaMasDineroGenerado != -1) {
            estadisticas.append("3) Какой стол приносит больше всех денег: Стол ").append(mesaMasDineroGenerado);
            int dineroGenerado = anticafe.obtenerDineroPorMesa().getOrDefault(mesaMasDineroGenerado, 0);
            estadisticas.append(", Доход: ").append(dineroGenerado).append("\n");
        } else {
            estadisticas.append("3) Нет занятыз столов, чтобы сосчитать доход.\n");
        }

        JOptionPane.showMessageDialog(null, estadisticas.toString());
    }

    private double calcularTiempoPromedioOcupacion() {
        long tiempoTotal = 0;
        int totalVecesOcupadas = 0;

        for (Mesa mesa : anticafe.getMesas().values()) {
            tiempoTotal += mesa.getTiempoAcumuladoOcupacion();
            totalVecesOcupadas += mesa.getVecesOcupada();
        }

        if (totalVecesOcupadas > 0) {
            return tiempoTotal / (double) totalVecesOcupadas;
        } else {
            return 0.0;
        }
    }


    private int encontrarMesaMasFrecuente() {
        int mesaMasFrecuente = -1;
        int maxFrecuencia = 0;

        for (Map.Entry<Integer, Mesa> entry : anticafe.getMesas().entrySet()) {
            Mesa mesa = entry.getValue();
            int vecesOcupada = mesa.getVecesOcupada();

            if (vecesOcupada > maxFrecuencia) {
                maxFrecuencia = vecesOcupada;
                mesaMasFrecuente = entry.getKey();
            }
        }

        return mesaMasFrecuente;
    }

    private int encontrarMesaMasDineroGenerado() {
        int mesaMasDineroGenerado = -1;
        int maxDineroGenerado = 0;
        for (Map.Entry<Integer, Integer> entry : anticafe.obtenerDineroPorMesa().entrySet()) {
            if (entry.getValue() > maxDineroGenerado) {
                maxDineroGenerado = entry.getValue();
                mesaMasDineroGenerado = entry.getKey();
            }
        }

        return mesaMasDineroGenerado;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AnticafeGUI();
            }
        });
    }
}