package com.apartmentservice.view.admin;
/**
 *
 * @author Nguyen Van Thang
 */
import com.apartmentservice.controller.ApartmentController;
import com.apartmentservice.model.Apartment;
import com.apartmentservice.model.BuildingSummary;
import com.apartmentservice.model.Invoice;
import com.apartmentservice.model.Resident;
import com.apartmentservice.model.Service;
import com.apartmentservice.utils.ReloadablePanel;
import com.apartmentservice.utils.Validator;
import com.apartmentservice.utils.XMLUtil;
import com.apartmentservice.wrapper.BuildingXML;
import com.apartmentservice.wrapper.InvoiceXML;
import com.apartmentservice.wrapper.ResidentXML;
import com.apartmentservice.wrapper.ServiceXML;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class ApartmentPanel extends javax.swing.JPanel implements ReloadablePanel{
    private DefaultTableModel tableModel;
    private ApartmentController controller;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private String loggedInUsername;
    
    public ApartmentPanel(String username) {
        initComponents();
        this.loggedInUsername = username;
        setLoggedInUsername(loggedInUsername);
        controller = new ApartmentController();
        tableModel = (DefaultTableModel) DanhSachCanHo.getModel();
        // Cho phép chọn nhiều dịch vụ
        listDichVu.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        DanhSachCanHo.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = DanhSachCanHo.getSelectedRow();
                if (selectedRow >= 0) {
                    showApartmentDetails(selectedRow);
                }
            }
        });
        loadComboToaTang();
        loadServiceList();

        checkDaO.addActionListener(e -> {
            if (checkDaO.isSelected()) checkBoTrong.setSelected(false);
        });
        checkBoTrong.addActionListener(e -> {
            if (checkBoTrong.isSelected()) checkDaO.setSelected(false);
        });

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        for (int i = 0; i < DanhSachCanHo.getColumnCount(); i++) {
            DanhSachCanHo.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
        }

        loadTable();
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        List<Apartment> apartments = controller.getAllApartments();
        int stt = 1;
        for (Apartment a : apartments) {
            tableModel.addRow(new Object[]{
                stt++, 
                a.getApartmentID(),
                a.getOwnerName(),
                a.getAcreage(),
                a.getBuilding(),
                a.getFloor(),
                a.getStatus(),
                a.getMemberNumber(),
                a.getServiceNames(),
                a.getMoveInDate()
            });
        }
    }
    private void loadComboToaTang() {
        try {
            // Đọc dữ liệu từ file building.xml
            File file = new File("data/buildings.xml");
            BuildingXML wrapper = XMLUtil.readFromXML(file, BuildingXML.class);
            List<BuildingSummary> buildingList = wrapper.getBuildings();

            // Xóa dữ liệu cũ
            comboToa.removeAllItems();
            comboTang.removeAllItems();

            // Thêm các tên Tòa vào comboToa
            if (buildingList != null) {
                for (BuildingSummary b : buildingList) {
                    comboToa.addItem(b.getName());
                }
            }

            // Sự kiện chọn Tòa
            comboToa.addActionListener(e -> {
                String selectedToa = (String) comboToa.getSelectedItem();
                comboTang.removeAllItems();
                if (selectedToa == null) return;

                for (BuildingSummary b : buildingList) {
                    if (b.getName().equals(selectedToa)) {
                        for (int i = 1; i <= b.getFloorCount(); i++) {
                            comboTang.addItem("Tầng " + i);
                        }
                        break;
                    }
                }
            });

            // Sự kiện chọn Tầng mà chưa chọn Tòa → cảnh báo
            comboTang.addActionListener(e -> {
                if (comboToa.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn Tòa trước khi chọn Tầng!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    comboTang.setSelectedItem(null); // reset lại lựa chọn;
                }
            });

            // Kích hoạt chọn mặc định Tòa đầu tiên (nếu có) để load Tầng
            if (comboToa.getItemCount() > 0) {
                comboToa.setSelectedIndex(0);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không thể tải danh sách Tòa từ file XML!", "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
        }
    }
    public void setLoggedInUsername(String username) {
        this.loggedInUsername = username;
        usernameTextField.setText(username);
        usernameTextField.setEditable(false);
        usernameTextField.setHorizontalAlignment(SwingConstants.CENTER);
       
    }
    private void clearForm() {
        txtChuHo.setText("");
        txtDienTich.setText("");
        txtSoNguoi.setText("");
        txtNgayVao.setDate(null);
        checkDaO.setSelected(false);
        checkBoTrong.setSelected(false);
        if (comboToa.getItemCount() > 0) {
            comboToa.setSelectedIndex(0);
        }
        if (comboTang.getItemCount() > 0) {
            comboTang.setSelectedIndex(0);
        }
        if (listDichVu != null) {
            listDichVu.clearSelection();
        }
    }
    
    private void loadServiceList() {
        try {
            // Đường dẫn tới file XML chứa danh sách dịch vụ
            File file = new File("data/services.xml");
            ServiceXML wrapper = XMLUtil.readFromXML(file, ServiceXML.class);
            List<Service> services = wrapper.getServices();

            // Tạo model mới cho listDichVu
            DefaultListModel<String> model = new DefaultListModel<>();
            if (services != null) {
                for (Service s : services) {
                    model.addElement(s.getServiceName()); // Chỉ hiển thị tên
                }
            }

            // Gán model cho listDichVu
            listDichVu.setModel(model);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Không thể tải danh sách dịch vụ từ services.xml\nChi tiết: " + e.getMessage(),
                "Lỗi khi tải dịch vụ",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showApartmentDetails(int row) {
        if (row < 0) return;

        // Lấy dữ liệu từng cột trong bảng
        String apartmentID = (String) tableModel.getValueAt(row, 1);
        String ownerName = (String) tableModel.getValueAt(row, 2);
        Double acreage = (Double) tableModel.getValueAt(row, 3);
        String building = (String) tableModel.getValueAt(row, 4);
        Integer floor = (Integer) tableModel.getValueAt(row, 5);
        String status = (String) tableModel.getValueAt(row, 6);
        Integer memberNumber = (Integer) tableModel.getValueAt(row, 7);
        String serviceNames = (String) tableModel.getValueAt(row, 8);
        String moveInDateStr = (String) tableModel.getValueAt(row, 9);

        // Set dữ liệu vào các trường
        txtChuHo.setText(ownerName);
        txtDienTich.setText(acreage != null ? acreage.toString() : "");
        txtSoNguoi.setText(memberNumber != null ? memberNumber.toString() : "");

        // Set combo Tòa
        comboToa.setSelectedItem(building);

        // Set combo Tầng (ví dụ comboTang có dạng "Tầng 1", "Tầng 2" ...)
        if (floor != null) {
            comboTang.setSelectedItem("Tầng " + floor);
        } else {
            comboTang.setSelectedIndex(-1);
        }

        // Set trạng thái checkbox
        if ("Đã ở".equalsIgnoreCase(status)) {
            checkDaO.setSelected(true);
            checkBoTrong.setSelected(false);
        } else if ("Bỏ trống".equalsIgnoreCase(status)) {
            checkDaO.setSelected(false);
            checkBoTrong.setSelected(true);
        } else {
            checkDaO.setSelected(false);
            checkBoTrong.setSelected(false);
        }

        // Set ngày vào ở
        try {
            java.util.Date date = sdf.parse(moveInDateStr);
            txtNgayVao.setDate(date);
        } catch (Exception ex) {
            txtNgayVao.setDate(null);
        }

        // Set dịch vụ được chọn trong listDichVu dựa theo serviceNames (chuỗi tên dịch vụ phân tách dấu ", ")
        if (serviceNames != null && !serviceNames.isEmpty()) {
            String[] selectedServices = serviceNames.split(",\\s*");
            List<Integer> indicesToSelect = new ArrayList<>();
            DefaultListModel<String> model = (DefaultListModel<String>) listDichVu.getModel();

            for (String service : selectedServices) {
                for (int i = 0; i < model.size(); i++) {
                    if (model.getElementAt(i).equalsIgnoreCase(service)) {
                        indicesToSelect.add(i);
                        break;
                    }
                }
            }
            // Chuyển list sang mảng int để chọn
            int[] selectedIndices = indicesToSelect.stream().mapToInt(i -> i).toArray();
            listDichVu.setSelectedIndices(selectedIndices);
        } else {
            listDichVu.clearSelection();
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel26 = new javax.swing.JPanel();
        jPanel25 = new javax.swing.JPanel();
        txtChuHo = new javax.swing.JTextField();
        lbChuCanHo11 = new javax.swing.JLabel();
        lbToa11 = new javax.swing.JLabel();
        lbTang11 = new javax.swing.JLabel();
        lbNgayVaoO11 = new javax.swing.JLabel();
        jSeparator12 = new javax.swing.JSeparator();
        usernameTextField = new javax.swing.JTextField();
        lbTaiKhoan11 = new javax.swing.JLabel();
        butClear = new javax.swing.JButton();
        lbSoNguoiO11 = new javax.swing.JLabel();
        txtSoNguoi = new javax.swing.JTextField();
        txtDienTich = new javax.swing.JTextField();
        lbDienTich11 = new javax.swing.JLabel();
        lbTrangThai11 = new javax.swing.JLabel();
        txtNgayVao = new com.toedter.calendar.JDateChooser();
        checkDaO = new javax.swing.JCheckBox();
        checkBoTrong = new javax.swing.JCheckBox();
        comboToa = new javax.swing.JComboBox<>();
        comboTang = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        listDichVu = new javax.swing.JList<>();
        butThem = new javax.swing.JButton();
        butCapNhat = new javax.swing.JButton();
        butXoa = new javax.swing.JButton();
        butSapXep = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        DanhSachCanHo = new javax.swing.JTable();
        butTimKiem = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        butLamMoi = new javax.swing.JButton();

        jPanel26.setBackground(new java.awt.Color(255, 255, 255));
        jPanel26.setPreferredSize(new java.awt.Dimension(920, 670));

        jPanel25.setBackground(new java.awt.Color(255, 255, 255));
        jPanel25.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Thông Tin Căn Hộ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N

        txtChuHo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtChuHoActionPerformed(evt);
            }
        });

        lbChuCanHo11.setText("Chủ Căn Hộ:");

        lbToa11.setText("Tòa: ");

        lbTang11.setText("Tầng: ");

        lbNgayVaoO11.setText("Ngày Vào Ở: ");

        jSeparator12.setOrientation(javax.swing.SwingConstants.VERTICAL);

        usernameTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usernameTextFieldActionPerformed(evt);
            }
        });

        lbTaiKhoan11.setText("Tài Khoản:");

        butClear.setText("Clear");
        butClear.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        butClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butClearActionPerformed(evt);
            }
        });

        lbSoNguoiO11.setText("Số Người Ở:");

        txtSoNguoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSoNguoiActionPerformed(evt);
            }
        });

        txtDienTich.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDienTichActionPerformed(evt);
            }
        });

        lbDienTich11.setText("Diện Tích:");

        lbTrangThai11.setText("Trạng Thái:");

        checkDaO.setText("Đã ở");
        checkDaO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkDaOActionPerformed(evt);
            }
        });

        checkBoTrong.setText("Bỏ trống");

        comboToa.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        comboTang.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel2.setText("Dịch Vụ:");

        listDichVu.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(listDichVu);

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel25Layout.createSequentialGroup()
                            .addComponent(lbDienTich11)
                            .addGap(39, 39, 39))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel25Layout.createSequentialGroup()
                            .addComponent(lbTrangThai11)
                            .addGap(33, 33, 33)))
                    .addGroup(jPanel25Layout.createSequentialGroup()
                        .addComponent(lbChuCanHo11)
                        .addGap(26, 26, 26)))
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel25Layout.createSequentialGroup()
                        .addComponent(txtChuHo, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(60, 60, 60)
                        .addComponent(lbToa11))
                    .addGroup(jPanel25Layout.createSequentialGroup()
                        .addComponent(txtDienTich, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(60, 60, 60)
                        .addComponent(lbTang11))
                    .addGroup(jPanel25Layout.createSequentialGroup()
                        .addComponent(checkDaO)
                        .addGap(18, 18, 18)
                        .addComponent(checkBoTrong)))
                .addGap(23, 23, 23)
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel25Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(122, 122, 122)
                        .addComponent(butClear, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38))
                    .addGroup(jPanel25Layout.createSequentialGroup()
                        .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(comboToa, 0, 110, Short.MAX_VALUE)
                            .addComponent(comboTang, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lbSoNguoiO11)
                            .addComponent(lbNgayVaoO11))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtNgayVao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtSoNguoi, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(27, 27, 27)))
                .addComponent(jSeparator12, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel25Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(usernameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel25Layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(lbTaiKhoan11)))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel25Layout.createSequentialGroup()
                        .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbToa11)
                            .addComponent(lbChuCanHo11)
                            .addComponent(txtChuHo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(comboToa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbTang11)
                            .addComponent(lbDienTich11)
                            .addComponent(txtDienTich, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(comboTang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(lbNgayVaoO11)
                    .addGroup(jPanel25Layout.createSequentialGroup()
                        .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtSoNguoi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbSoNguoiO11))
                        .addGap(18, 18, 18)
                        .addComponent(txtNgayVao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10)
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lbTrangThai11)
                        .addComponent(checkDaO)
                        .addComponent(checkBoTrong)
                        .addComponent(butClear)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel25Layout.createSequentialGroup()
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel25Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jSeparator12, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel25Layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(lbTaiKhoan11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(usernameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        butThem.setText("Thêm");
        butThem.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        butThem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butThemActionPerformed(evt);
            }
        });

        butCapNhat.setText("Cập Nhật");
        butCapNhat.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        butCapNhat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butCapNhatActionPerformed(evt);
            }
        });

        butXoa.setText("Xóa");
        butXoa.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        butXoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butXoaActionPerformed(evt);
            }
        });

        butSapXep.setText("Sắp Xếp");
        butSapXep.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        butSapXep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSapXepActionPerformed(evt);
            }
        });

        DanhSachCanHo.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        DanhSachCanHo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "STT", "Mã Căn Hộ", "Chủ Căn Hộ", "Diện Tích", "Tòa", "Tầng", "Trạng Thái", "Số Người Ở", "Dịch Vụ", "Ngày Vào Ở"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(DanhSachCanHo);

        butTimKiem.setText("Tìm Kiếm");
        butTimKiem.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        butTimKiem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butTimKiemActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel12.setText("DANH SÁCH CĂN HỘ");

        butLamMoi.setText("Làm Mới");
        butLamMoi.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        butLamMoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butLamMoiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel26Layout.createSequentialGroup()
                        .addGap(378, 378, 378)
                        .addComponent(jLabel12))
                    .addGroup(jPanel26Layout.createSequentialGroup()
                        .addGap(256, 256, 256)
                        .addComponent(butThem, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(butCapNhat, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(butXoa, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(butSapXep, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(butTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(butLamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel26Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1))
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jPanel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 33, Short.MAX_VALUE))
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(butThem)
                    .addComponent(butCapNhat)
                    .addComponent(butXoa)
                    .addComponent(butSapXep)
                    .addComponent(butTimKiem)
                    .addComponent(butLamMoi))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 930, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1028, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel26, javax.swing.GroupLayout.DEFAULT_SIZE, 1016, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1180, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel26, javax.swing.GroupLayout.DEFAULT_SIZE, 1174, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtChuHoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtChuHoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtChuHoActionPerformed

    private void usernameTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usernameTextFieldActionPerformed

    }//GEN-LAST:event_usernameTextFieldActionPerformed

    private void butClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butClearActionPerformed
        clearForm();
    }//GEN-LAST:event_butClearActionPerformed

    private void txtSoNguoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSoNguoiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSoNguoiActionPerformed

    private void txtDienTichActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDienTichActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDienTichActionPerformed

    private void checkDaOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkDaOActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_checkDaOActionPerformed

    private void butThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butThemActionPerformed
        // Lấy dữ liệu từ form
        String toa = (String) comboToa.getSelectedItem();
        String tangStr = (String) comboTang.getSelectedItem();
        String chuHo = txtChuHo.getText().trim();
        String dienTichStr = txtDienTich.getText().trim();
        String soNguoiStr = txtSoNguoi.getText().trim();
        java.util.Date date = txtNgayVao.getDate();

        // Kiểm tra dữ liệu bắt buộc và hợp lệ
        if (toa == null || tangStr == null || chuHo.isEmpty() || dienTichStr.isEmpty() ||
            soNguoiStr.isEmpty() || date == null ||
            (!checkDaO.isSelected() && !checkBoTrong.isSelected()) ||
            (checkDaO.isSelected() && checkBoTrong.isSelected())) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ và hợp lệ thông tin!\n(Trạng thái chỉ được chọn một)", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!Validator.isValidName(chuHo)) {
            JOptionPane.showMessageDialog(this, "Tên chủ hộ không hợp lệ!", "Lỗi nhập", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!Validator.isPositiveDouble(dienTichStr)) {
            JOptionPane.showMessageDialog(this, "Diện tích phải là số thực dương!", "Lỗi nhập", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!Validator.isPositiveInteger(soNguoiStr)) {
            JOptionPane.showMessageDialog(this, "Số người ở phải là số nguyên dương!", "Lỗi nhập", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Kiểm tra ngày vào ở không vượt quá hiện tại
        LocalDate ngayVao = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if (ngayVao.isAfter(LocalDate.now())) {
            JOptionPane.showMessageDialog(this, "Ngày vào ở không được vượt quá ngày hiện tại!", "Lỗi ngày", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Lấy danh sách dịch vụ đã chọn
        List<String> selectedServices = listDichVu.getSelectedValuesList();
        String dichVuStr = String.join(", ", selectedServices); // ghép bằng dấu phẩy

        try {
            // Trích số tầng từ combo "Tầng 1"
            int tang = Integer.parseInt(tangStr.replaceAll("[^\\d]", ""));
            double dienTich = Double.parseDouble(dienTichStr);
            int soNguoi = Integer.parseInt(soNguoiStr);
            String trangThai = checkDaO.isSelected() ? "Đã ở" : "Bỏ trống";
            String ngayVaoStr = sdf.format(date); // sdf là SimpleDateFormat("dd/MM/yyyy")

            // Gọi controller để thêm căn hộ kèm dịch vụ
            controller.addApartment(toa, tang, dienTich, trangThai, chuHo, soNguoi, dichVuStr, ngayVaoStr);

            JOptionPane.showMessageDialog(this, "Thêm căn hộ thành công!");
            loadTable();
            clearForm();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi: " + e.getMessage(), "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_butThemActionPerformed

    private void butCapNhatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butCapNhatActionPerformed
        List<Apartment> apartments = controller.getAllApartments();
        if (apartments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có căn hộ nào để cập nhật.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        // Hộp thoại chọn mã căn hộ
        String[] ids = apartments.stream().map(Apartment::getApartmentID).toArray(String[]::new);
        String id = (String) JOptionPane.showInputDialog(
            this,
            "Chọn mã căn hộ để cập nhật:",
            "Cập nhật căn hộ",
            JOptionPane.PLAIN_MESSAGE,
            null,
            ids,
            ids[0]
        );
        if (id == null || id.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã căn hộ không được để trống!");
            return;
        }
        // Sử dụng controller để tìm căn hộ
        List<Apartment> foundList = controller.findByApartmentID(id.trim());
        if (foundList == null || foundList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy căn hộ có mã: " + id);
            return;
        }
        Apartment existing = foundList.get(0);
        // Lấy dữ liệu từ form
        String owner = txtChuHo.getText().trim();
        String acreageStr = txtDienTich.getText().trim();
        String memberStr = txtSoNguoi.getText().trim();
        String building = (String) comboToa.getSelectedItem();
        String floorStr = (String) comboTang.getSelectedItem();
        String status = checkDaO.isSelected() ? "Đã ở" : (checkBoTrong.isSelected() ? "Bỏ trống" : "");
        java.util.Date moveInDateUtil = txtNgayVao.getDate();
        List<String> selectedServices = listDichVu.getSelectedValuesList();
        String dichVuStr = String.join(", ", selectedServices);
        // Kiểm tra dữ liệu
        if (owner.isEmpty() || acreageStr.isEmpty() || memberStr.isEmpty() ||
            building == null || floorStr == null || moveInDateUtil == null || status.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }
        if (!Validator.isPositiveDouble(acreageStr)) {
            JOptionPane.showMessageDialog(this, "Diện tích phải là số thực dương!");
            return;
        }
        if (!Validator.isPositiveInteger(memberStr)) {
            JOptionPane.showMessageDialog(this, "Số người phải là số nguyên dương!");
            return;
        }
        try {
            int floor = Integer.parseInt(floorStr.replaceAll("[^\\d]", ""));
            double acreage = Double.parseDouble(acreageStr);
            int members = Integer.parseInt(memberStr);
            String moveInDate = sdf.format(moveInDateUtil);
            // Gọi controller cập nhật (sử dụng updateApartment đã có)
            controller.updateApartment(id.trim(), building, floor, acreage, status, owner, members, dichVuStr, moveInDate);

            // Cập nhật tên khách hàng trong residents.xml
            File residentsFile = new File("data/residents.xml");
            if (residentsFile.exists()) {
                ResidentXML residentsWrapper = XMLUtil.readFromXML(residentsFile, ResidentXML.class);
                if (residentsWrapper != null) {
                    List<Resident> residentsList = residentsWrapper.getResidents();
                    for (Resident resident : residentsList) {
                        if (resident.getApartmentID().equals(id)) {
                            resident.setName(owner);  // Cập nhật tên chủ sở hữu
                        }
                    }
                    XMLUtil.writeToXML(residentsFile, residentsWrapper, ResidentXML.class);
                }
            }

            // Cập nhật tên khách hàng trong invoices.xml
            File invoicesFile = new File("data/invoices.xml");
            if (invoicesFile.exists()) {
                InvoiceXML invoiceWrapper = XMLUtil.readFromXML(invoicesFile, InvoiceXML.class);
                if (invoiceWrapper != null) {
                    List<Invoice> invoiceList = invoiceWrapper.getInvoices();
                    for (Invoice invoice : invoiceList) {
                        if (invoice.getApartmentID().equals(id)) {
                            invoice.setCustomerName(owner);  // Cập nhật tên khách hàng
                        }
                    }
                    XMLUtil.writeToXML(invoicesFile, invoiceWrapper, InvoiceXML.class);
                }
            }

            JOptionPane.showMessageDialog(this, "Cập nhật thành công căn hộ có mã: " + id);
            loadTable();
            clearForm();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi: " + e.getMessage(), "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_butCapNhatActionPerformed

    private void butXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butXoaActionPerformed
        List<Apartment> apartments = controller.getAllApartments();
        if (apartments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có căn hộ nào để xóa.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        // Lấy danh sách mã căn hộ
        String[] ids = apartments.stream()
        .map(Apartment::getApartmentID)
        .toArray(String[]::new);
        // Cho người dùng chọn mã căn hộ để xóa
        String selectedID = (String) JOptionPane.showInputDialog(
            this,
            "Chọn mã căn hộ cần xóa:",
            "Xóa căn hộ",
            JOptionPane.PLAIN_MESSAGE,
            null,
            ids,
            ids[0]
        );
        if (selectedID == null || selectedID.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Bạn chưa chọn mã căn hộ.");
            return;
        }
        // Kiểm tra căn hộ có tồn tại (findByApartmentID trả về List)
        List<Apartment> found = controller.findByApartmentID(selectedID.trim());
        if (found == null || found.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy căn hộ có mã: " + selectedID, "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Apartment a = found.get(0);
        // Xác nhận xóa
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn xóa căn hộ: " + a.getApartmentID() + "?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = controller.deleteApartment(a.getApartmentID());
            if (success) {
                JOptionPane.showMessageDialog(this, "Đã xóa căn hộ thành công.");
                loadTable();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại. Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_butXoaActionPerformed

    private void butSapXepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSapXepActionPerformed
        String[] criteria = {"Mã căn hộ", "Chủ hộ", "Diện tích", "Tòa", "Tầng", "Số người"};
        String[] order = {"Tăng dần", "Giảm dần"};

        String selectedCriteria = (String) JOptionPane.showInputDialog(
            this,
            "Chọn tiêu chí sắp xếp:",
            "Sắp xếp căn hộ",
            JOptionPane.QUESTION_MESSAGE,
            null,
            criteria,
            criteria[0]
        );
        if (selectedCriteria == null) return;

        String selectedOrder = (String) JOptionPane.showInputDialog(
            this,
            "Chọn thứ tự sắp xếp:",
            "Thứ tự sắp xếp",
            JOptionPane.QUESTION_MESSAGE,
            null,
            order,
            order[0]
        );
        if (selectedOrder == null) return;

        boolean ascending = selectedOrder.equals("Tăng dần");

        List<Apartment> list = controller.sort(selectedCriteria, ascending);
        if (list != null) {
            tableModel.setRowCount(0);
            int i = 1;
            for (Apartment a : list) {
                tableModel.addRow(new Object[]{
                    i++,
                    a.getApartmentID(),
                    a.getOwnerName(),
                    a.getAcreage(),
                    a.getBuilding(),
                    a.getFloor(),
                    a.getStatus(),
                    a.getMemberNumber(),
                    a.getServiceNames(),
                    a.getMoveInDate()
                });
            }
        }
    }//GEN-LAST:event_butSapXepActionPerformed

    private void butTimKiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butTimKiemActionPerformed
        String[] criteria = {"Mã căn hộ", "Chủ hộ", "Tòa", "Trạng thái", "Ngày vào"};
        String selected = (String) JOptionPane.showInputDialog(
            this,
            "Chọn tiêu chí tìm kiếm:",
            "Tìm kiếm",
            JOptionPane.PLAIN_MESSAGE,
            null,
            criteria,
            criteria[0]
        );
        if (selected == null) return;

        List<Apartment> results = new ArrayList<>();
        try {
            switch (selected) {
                case "Mã căn hộ": {
                    String maCanHo = JOptionPane.showInputDialog(this, "Nhập mã căn hộ:");
                    if (maCanHo != null && !maCanHo.trim().isEmpty()) {
                        results = controller.findByApartmentID(maCanHo.trim());
                    }
                    break;
                }
                case "Chủ hộ": {
                    String owner = JOptionPane.showInputDialog(this, "Nhập tên chủ hộ:");
                    if (owner != null && !owner.trim().isEmpty()) {
                        results = controller.findByOwnerName(owner.trim());
                    }
                    break;
                }
                case "Tòa": {
                    String building = JOptionPane.showInputDialog(this, "Nhập tên tòa:");
                    if (building != null && !building.trim().isEmpty()) {
                        results = controller.findByBuilding(building.trim());
                    }
                    break;
                }
                case "Trạng thái": {
                    String status = JOptionPane.showInputDialog(this, "Nhập trạng thái (Đã ở/Bỏ trống):");
                    if (status != null && !status.trim().isEmpty()) {
                        results = controller.findByStatus(status.trim());
                    }
                    break;
                }
                case "Ngày vào": {
                    String moveInDate = JOptionPane.showInputDialog(this, "Nhập ngày vào (dd/mm/yyyy):");
                    if (moveInDate != null && !moveInDate.trim().isEmpty()) {
                        results = controller.findByMoveInDate(moveInDate.trim());
                    }
                    break;
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm: " + ex.getMessage());
            return;
        }

        tableModel.setRowCount(0);
        if (results == null || results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy kết quả phù hợp.");
        } else {
            int i = 1;
            for (Apartment a : results) {
                tableModel.addRow(new Object[]{
                    i++,
                    a.getApartmentID(),
                    a.getOwnerName(),
                    a.getAcreage(),
                    a.getBuilding(),
                    a.getFloor(),
                    a.getStatus(),
                    a.getMemberNumber(),
                    a.getServiceNames(),
                    a.getMoveInDate()
                });
            }
        }
    }//GEN-LAST:event_butTimKiemActionPerformed

    private void butLamMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butLamMoiActionPerformed
        loadTable();
        clearForm();
        JOptionPane.showMessageDialog(this, "Dữ liệu đã được làm mới.");
    }//GEN-LAST:event_butLamMoiActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable DanhSachCanHo;
    private javax.swing.JButton butCapNhat;
    private javax.swing.JButton butClear;
    private javax.swing.JButton butLamMoi;
    private javax.swing.JButton butSapXep;
    private javax.swing.JButton butThem;
    private javax.swing.JButton butTimKiem;
    private javax.swing.JButton butXoa;
    private javax.swing.JCheckBox checkBoTrong;
    private javax.swing.JCheckBox checkDaO;
    private javax.swing.JComboBox<String> comboTang;
    private javax.swing.JComboBox<String> comboToa;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JLabel lbChuCanHo11;
    private javax.swing.JLabel lbDienTich11;
    private javax.swing.JLabel lbNgayVaoO11;
    private javax.swing.JLabel lbSoNguoiO11;
    private javax.swing.JLabel lbTaiKhoan11;
    private javax.swing.JLabel lbTang11;
    private javax.swing.JLabel lbToa11;
    private javax.swing.JLabel lbTrangThai11;
    private javax.swing.JList<String> listDichVu;
    private javax.swing.JTextField txtChuHo;
    private javax.swing.JTextField txtDienTich;
    private com.toedter.calendar.JDateChooser txtNgayVao;
    private javax.swing.JTextField txtSoNguoi;
    private javax.swing.JTextField usernameTextField;
    // End of variables declaration//GEN-END:variables

    @Override
    public void reload() {
        setLoggedInUsername(loggedInUsername);
        controller = new ApartmentController();
        tableModel = (DefaultTableModel) DanhSachCanHo.getModel();
        // Cho phép chọn nhiều dịch vụ
        listDichVu.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        DanhSachCanHo.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = DanhSachCanHo.getSelectedRow();
                if (selectedRow >= 0) {
                    showApartmentDetails(selectedRow);
                }
            }
        });
        //loadComboToaTang();
        loadServiceList();

        checkDaO.addActionListener(e -> {
            if (checkDaO.isSelected()) checkBoTrong.setSelected(false);
        });
        checkBoTrong.addActionListener(e -> {
            if (checkBoTrong.isSelected()) checkDaO.setSelected(false);
        });

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        for (int i = 0; i < DanhSachCanHo.getColumnCount(); i++) {
            DanhSachCanHo.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
        }

        loadTable();
    }
}
