package com.apartmentservice.view.admin;

import com.apartmentservice.controller.ApartmentController;
import com.apartmentservice.controller.InvoiceController;
import com.apartmentservice.controller.ServiceController;
import com.apartmentservice.manager.InvoiceManager;
import com.apartmentservice.manager.ServiceManager;
import com.apartmentservice.model.Apartment;
import com.apartmentservice.model.Invoice;
import com.apartmentservice.model.Service;
import com.apartmentservice.utils.IDGenerator;
import com.apartmentservice.utils.ReloadablePanel;
import com.apartmentservice.utils.Validator;
import com.apartmentservice.utils.XMLUtil;
import com.apartmentservice.wrapper.ApartmentXML;
import com.apartmentservice.wrapper.ServiceXML;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Set;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Nguyen Van Thang
 */
public class ListInvoicePanel extends javax.swing.JPanel implements ReloadablePanel{

    private DefaultTableModel tableModel;
    private InvoiceController controller;
    private ServiceController serviceController;
    private ApartmentController apartmentController;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private String loggedInUsername;
    private boolean isUserSelectingCombo = true;
    public ListInvoicePanel(String username) {
        initComponents();
        this.loggedInUsername = username;
        controller = new InvoiceController();
        apartmentController = new ApartmentController();
        serviceController = new ServiceController();
        tableModel = (DefaultTableModel) DanhSachHoaDonThanhToan.getModel();
        try {
            loadCanHo();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi load dữ liệu căn hộ:\n" + e.getMessage());
        }
        // CheckBox chỉ chọn 1 trạng thái thanh toán
        checkDaThanhToan.addActionListener(e -> checkChuaThanhToan.setSelected(!checkDaThanhToan.isSelected()));
        checkChuaThanhToan.addActionListener(e -> checkDaThanhToan.setSelected(!checkChuaThanhToan.isSelected()));
        DanhSachHoaDonThanhToan.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = DanhSachHoaDonThanhToan.getSelectedRow();
                if (selectedRow >= 0) {
                    isUserSelectingCombo = false; // Tạm tắt sự kiện combo khi set dữ liệu
                    String invoiceID = (String) DanhSachHoaDonThanhToan.getValueAt(selectedRow, 1);
                    String apartmentID = (String) DanhSachHoaDonThanhToan.getValueAt(selectedRow, 2);
                    String customerName = (String) DanhSachHoaDonThanhToan.getValueAt(selectedRow, 3);
                    String cashierName = (String) DanhSachHoaDonThanhToan.getValueAt(selectedRow, 4);
                    String invoiceDateStr = (String) DanhSachHoaDonThanhToan.getValueAt(selectedRow, 5);
                    String totalAmount = (String) DanhSachHoaDonThanhToan.getValueAt(selectedRow, 6);
                    String status = (String) DanhSachHoaDonThanhToan.getValueAt(selectedRow, 7);

                    comboMaCanHo.setSelectedItem(apartmentID);
                    comboKhachHang.setSelectedItem(customerName);
                    txtThuNgan.setText(cashierName);
                    txtTongThanhToan.setText(totalAmount);
                    try {
                        if (invoiceDateStr != null && !invoiceDateStr.trim().isEmpty()) {
                            Date date = sdf.parse(invoiceDateStr);
                            dateNgayThanhToan.setDate(date);
                        } else {
                            dateNgayThanhToan.setDate(null);
                        }
                    } catch (Exception ex) {
                        dateNgayThanhToan.setDate(null);
                    }
                    if ("Đã thanh toán".equalsIgnoreCase(status)) {
                        checkDaThanhToan.setSelected(true);
                        checkChuaThanhToan.setSelected(false);
                    } else if ("Chưa thanh toán".equalsIgnoreCase(status)) {
                        checkDaThanhToan.setSelected(false);
                        checkChuaThanhToan.setSelected(true);
                    } else {
                        checkDaThanhToan.setSelected(false);
                        checkChuaThanhToan.setSelected(false);
                    }
                    isUserSelectingCombo = true; // Bật lại sự kiện combo
                }
            }
        });
        loadTable();
        // Set căn lề trái cho tất cả cột
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        for (int i = 0; i < DanhSachHoaDonThanhToan.getColumnCount(); i++) {
            DanhSachHoaDonThanhToan.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
        }
    }
    private void loadTable() {
        tableModel.setRowCount(0); // Xóa dữ liệu cũ
        List<Invoice> invoices = controller.getAllInvoices();
        List<Service> services = serviceController.getAllServices();
        List<Apartment> apartments = apartmentController.getAllApartments();
        if (invoices == null || invoices.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có hóa đơn nào để hiển thị.");
            return;
        }
        int stt = 1;
        for (Invoice invoice : invoices) {
            // Tìm căn hộ tương ứng với mã căn hộ trong hóa đơn
            Apartment matchedApartment = apartments.stream()
                    .filter(a -> a.getApartmentID().equals(invoice.getApartmentID()))
                    .findFirst()
                    .orElse(null);
            String totalFormatted = "0";
            if (matchedApartment != null) {
                totalFormatted = Invoice.getTotalAmountFormatted(matchedApartment, services);
            }
            String status = invoice.isPaidAsBoolean() ? "Đã thanh toán" : "Chưa thanh toán";
            tableModel.addRow(new Object[]{
                stt++,
                invoice.getInvoiceID(),
                invoice.getApartmentID(),
                invoice.getCustomerName(),
                invoice.getCashierName(),
                invoice.getInvoiceDate(),
                totalFormatted,
                status
            });
        }
    }

    private void loadCanHo() {
        comboMaCanHo.removeAllItems();
        comboKhachHang.removeAllItems();
        File apartmentFile = new File("data/apartments.xml");
        File serviceFile = new File("data/services.xml");
        try {
            ApartmentXML apartmentWrapper = XMLUtil.readFromXML(apartmentFile, ApartmentXML.class);
            List<Apartment> apartmentList = (apartmentWrapper != null) ? apartmentWrapper.getApartment() : null;

            if (apartmentList == null || apartmentList.isEmpty()) {
                throw new NullPointerException("Danh sách apartment null - có thể file rỗng hoặc sai format.");
            }
            ServiceXML serviceWrapper = XMLUtil.readFromXML(serviceFile, ServiceXML.class);
            List<Service> serviceList = (serviceWrapper != null) ? serviceWrapper.getServices() : null;

            if (serviceList == null || serviceList.isEmpty()) {
                throw new NullPointerException("Danh sách service null - có thể file rỗng hoặc sai format.");
            }
            // Thêm dữ liệu vào ComboBox
            for (Apartment a : apartmentList) {
                comboMaCanHo.addItem(a.getApartmentID());
                comboKhachHang.addItem(a.getOwnerName());
            }

            // Xóa tất cả ActionListener cũ trước khi thêm mới
            removeAllActionListeners(comboMaCanHo);
            removeAllActionListeners(comboKhachHang);

            comboMaCanHo.addActionListener(e -> {
                if (isUserSelectingCombo) {  // Chỉ xử lý khi người dùng chọn
                    // Khi chọn mã căn hộ, reset combo khách hàng
                    isUserSelectingCombo = false;
                    comboKhachHang.setSelectedIndex(-1);
                    isUserSelectingCombo = true;

                    String selectedID = (String) comboMaCanHo.getSelectedItem();
                    Apartment found = apartmentList.stream()
                            .filter(a -> a.getApartmentID().equals(selectedID))
                            .findFirst()
                            .orElse(null);
                    String formatted = (found != null)
                            ? Invoice.getTotalAmountFormatted(found, serviceList)
                            : "0";
                    txtTongThanhToan.setText(formatted + " VNĐ");
                }
            });

            comboKhachHang.addActionListener(e -> {
                if (isUserSelectingCombo) {
                    // Khi chọn khách hàng, reset combo mã căn hộ
                    isUserSelectingCombo = false;
                    comboMaCanHo.setSelectedIndex(-1);
                    isUserSelectingCombo = true;

                    String selectedName = (String) comboKhachHang.getSelectedItem();
                    Apartment found = apartmentList.stream()
                            .filter(a -> a.getOwnerName().equals(selectedName))
                            .findFirst()
                            .orElse(null);
                    String formatted = (found != null)
                            ? Invoice.getTotalAmountFormatted(found, serviceList)
                            : "0";
                    txtTongThanhToan.setText(formatted + " VNĐ");
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi tải dữ liệu căn hộ:\n" + ex.getMessage(),
                    "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
        }
    }
        // Hàm tiện ích xóa toàn bộ ActionListener khỏi JComboBox
    private void removeAllActionListeners(javax.swing.JComboBox comboBox) {
        for (java.awt.event.ActionListener al : comboBox.getActionListeners()) {
            comboBox.removeActionListener(al);
        }
    }
    
    private void clearForm() {
        comboMaCanHo.setSelectedIndex(-1);
        comboKhachHang.setSelectedIndex(-1);
        txtThuNgan.setText("");
        txtTongThanhToan.setText("");
        dateNgayThanhToan.setDate(null);
        checkDaThanhToan.setSelected(false);
        checkChuaThanhToan.setSelected(false);
        DanhSachHoaDonThanhToan.clearSelection();
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        lbMaCanHo = new javax.swing.JLabel();
        lbNgayThanhToan = new javax.swing.JLabel();
        lbKhachHang = new javax.swing.JLabel();
        lbThuNgan = new javax.swing.JLabel();
        lbTongThanhToan = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lbTrangThai = new javax.swing.JLabel();
        checkDaThanhToan = new javax.swing.JCheckBox();
        checkChuaThanhToan = new javax.swing.JCheckBox();
        butClear = new javax.swing.JButton();
        dateNgayThanhToan = new com.toedter.calendar.JDateChooser();
        comboMaCanHo = new javax.swing.JComboBox<>();
        txtThuNgan = new javax.swing.JTextField();
        txtTongThanhToan = new javax.swing.JTextField();
        comboKhachHang = new javax.swing.JComboBox<>();
        butCapNhat = new javax.swing.JButton();
        butThem = new javax.swing.JButton();
        butXoa = new javax.swing.JButton();
        butTimKiem = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        DanhSachHoaDonThanhToan = new javax.swing.JTable();
        jLabel17 = new javax.swing.JLabel();
        butSapXep = new javax.swing.JButton();
        butLamMoi = new javax.swing.JButton();
        butThanhToan = new javax.swing.JButton();

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setPreferredSize(new java.awt.Dimension(920, 650));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Thông Tin Danh Sách Thanh Toán", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N

        lbMaCanHo.setText("Mã Căn Hộ: ");

        lbNgayThanhToan.setText("Ngày Thanh Toán:");

        lbKhachHang.setText("Khách Hàng: ");

        lbThuNgan.setText("Thu ngân:");

        lbTongThanhToan.setText("Tổng Thanh Toán: ");

        jLabel2.setText("VNĐ");

        lbTrangThai.setText("Trạng Thái:");

        checkDaThanhToan.setText("Đã Thanh Toán");

        checkChuaThanhToan.setText("Chưa Thanh Toán");
        checkChuaThanhToan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkChuaThanhToanActionPerformed(evt);
            }
        });

        butClear.setText("Clear");
        butClear.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        butClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butClearActionPerformed(evt);
            }
        });

        comboMaCanHo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        txtThuNgan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtThuNganActionPerformed(evt);
            }
        });

        comboKhachHang.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(375, 375, 375)
                        .addComponent(butClear, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(73, 73, 73)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(lbMaCanHo)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(comboMaCanHo, 0, 201, Short.MAX_VALUE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lbKhachHang)
                                    .addComponent(lbThuNgan))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtThuNgan)
                                    .addComponent(comboKhachHang, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGap(30, 30, 30)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lbTongThanhToan)
                                    .addComponent(lbTrangThai))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(checkDaThanhToan)
                                        .addGap(18, 18, 18)
                                        .addComponent(checkChuaThanhToan))
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addGap(209, 209, 209)
                                        .addComponent(jLabel2))))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(lbNgayThanhToan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(dateNgayThanhToan, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtTongThanhToan, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addGap(100, 100, 100))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lbMaCanHo)
                        .addComponent(lbNgayThanhToan)
                        .addComponent(comboMaCanHo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(dateNgayThanhToan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbKhachHang)
                    .addComponent(lbTongThanhToan)
                    .addComponent(jLabel2)
                    .addComponent(txtTongThanhToan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboKhachHang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbThuNgan)
                    .addComponent(lbTrangThai)
                    .addComponent(checkDaThanhToan)
                    .addComponent(checkChuaThanhToan)
                    .addComponent(txtThuNgan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(butClear)
                .addGap(8, 8, 8))
        );

        butCapNhat.setText("Cập Nhật");
        butCapNhat.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        butCapNhat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butCapNhatActionPerformed(evt);
            }
        });

        butThem.setText("Thêm");
        butThem.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        butThem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butThemActionPerformed(evt);
            }
        });

        butXoa.setText("Xóa");
        butXoa.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        butXoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butXoaActionPerformed(evt);
            }
        });

        butTimKiem.setText("Tìm Kiếm");
        butTimKiem.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        butTimKiem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butTimKiemActionPerformed(evt);
            }
        });

        DanhSachHoaDonThanhToan.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        DanhSachHoaDonThanhToan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "STT", "Mã Hóa Đơn", "Mã Căn Hộ", "Khách Hàng", "Thu Ngân", "Ngày Thanh Toán", "Tổng Tiền", "Trạng Thái"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(DanhSachHoaDonThanhToan);

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel17.setText("DANH SÁCH HÓA ĐƠN THANH TOÁN");

        butSapXep.setText("Sắp Xếp");
        butSapXep.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        butSapXep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSapXepActionPerformed(evt);
            }
        });

        butLamMoi.setText("Làm Mới");
        butLamMoi.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        butLamMoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butLamMoiActionPerformed(evt);
            }
        });

        butThanhToan.setText("Thanh Toán");
        butThanhToan.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        butThanhToan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butThanhToanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(336, 336, 336)
                                .addComponent(jLabel17))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(91, 91, 91)
                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 818, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 102, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(229, 229, 229)
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
                .addComponent(butLamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(butThanhToan, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(butThem)
                    .addComponent(butCapNhat)
                    .addComponent(butXoa)
                    .addComponent(butTimKiem)
                    .addComponent(butSapXep)
                    .addComponent(butLamMoi)
                    .addComponent(butThanhToan))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 940, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1029, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 1017, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1205, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 1199, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void checkChuaThanhToanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkChuaThanhToanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_checkChuaThanhToanActionPerformed

    private void butClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butClearActionPerformed
        clearForm();
    }//GEN-LAST:event_butClearActionPerformed

    private void txtThuNganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtThuNganActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtThuNganActionPerformed

    private void butCapNhatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butCapNhatActionPerformed
        try {
            List<Invoice> invoices = controller.getAllInvoices();
            if (invoices.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không có hóa đơn nào để cập nhật.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String[] ids = invoices.stream().map(Invoice::getInvoiceID).toArray(String[]::new);
            String selectedID = (String) JOptionPane.showInputDialog(
                this,
                "Chọn mã hóa đơn để cập nhật:",
                "Cập nhật hóa đơn",
                JOptionPane.PLAIN_MESSAGE,
                null,
                ids,
                ids[0]
            );

            if (selectedID == null || selectedID.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Mã hóa đơn không được để trống!");
                return;
            }

            Invoice existing = controller.findByInvoiceID(selectedID);
            if (existing == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn có mã: " + selectedID);
                return;
            }

            Object selectedMaCanHo = comboMaCanHo.getSelectedItem();
            Object selectedKhachHang = comboKhachHang.getSelectedItem();
            boolean maCanHoSelected = selectedMaCanHo != null && !selectedMaCanHo.toString().trim().isEmpty();
            boolean khachHangSelected = selectedKhachHang != null && !selectedKhachHang.toString().trim().isEmpty();

            if (maCanHoSelected && khachHangSelected) {
                JOptionPane.showMessageDialog(this, "Vui lòng chỉ chọn MỘT trong hai: Mã căn hộ HOẶC Họ tên khách hàng.");
                return;
            } else if (!maCanHoSelected && !khachHangSelected) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn Mã căn hộ hoặc Họ tên khách hàng.");
                return;
            }

            List<Apartment> apartmentList = apartmentController.getAllApartments();
            if (apartmentList == null || apartmentList.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy dữ liệu căn hộ.");
                return;
            }

            String apartmentID = "";
            String customer = "";
            if (maCanHoSelected) {
                apartmentID = selectedMaCanHo.toString();
                final String finalID = apartmentID;
                customer = apartmentList.stream()
                .filter(a -> a.getApartmentID().equals(finalID))
                .map(Apartment::getOwnerName)
                .findFirst().orElse("");
            } else {
                customer = selectedKhachHang.toString();
                final String finalName = customer;
                apartmentID = apartmentList.stream()
                .filter(a -> a.getOwnerName().equals(finalName))
                .map(Apartment::getApartmentID)
                .findFirst().orElse("");
            }

            String cashier = txtThuNgan.getText().trim();
            Date selectedDate = dateNgayThanhToan.getDate();
            String paid = checkDaThanhToan.isSelected() ? "Đã thanh toán"
            : (checkChuaThanhToan.isSelected() ? "Chưa thanh toán" : "");

            if (apartmentID.isEmpty() || customer.isEmpty() || cashier.isEmpty() || selectedDate == null || paid.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
                return;
            }
            if (!Validator.isValidName(customer)) {
                JOptionPane.showMessageDialog(this, "Tên khách hàng không hợp lệ!");
                return;
            }
            if (!Validator.isValidName(cashier)) {
                JOptionPane.showMessageDialog(this, "Tên thu ngân không hợp lệ!");
                return;
            }

            String invoiceDate = sdf.format(selectedDate);
            List<Service> existingFees = existing.getService();
            Invoice updated = new Invoice(
                selectedID,
                apartmentID,
                customer,
                cashier,
                invoiceDate,
                paid,
                existingFees
            );

            controller.updateInvoice(updated);
            loadTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Cập nhật hóa đơn thành công!");

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Đã xảy ra lỗi khi cập nhật hóa đơn:\n" + ex.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_butCapNhatActionPerformed

    private void butThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butThemActionPerformed
        try {
            Object selectedMaCanHo = comboMaCanHo.getSelectedItem();
            Object selectedKhachHang = comboKhachHang.getSelectedItem();

            boolean hasMaCanHo = selectedMaCanHo != null && !selectedMaCanHo.toString().trim().isEmpty();
            boolean hasKhachHang = selectedKhachHang != null && !selectedKhachHang.toString().trim().isEmpty();

            if (hasMaCanHo && hasKhachHang) {
                JOptionPane.showMessageDialog(this, "Vui lòng chỉ chọn MỘT trong hai: Mã căn hộ HOẶC Tên khách hàng.");
                return;
            }
            if (!hasMaCanHo && !hasKhachHang) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn Mã căn hộ hoặc Tên khách hàng.");
                return;
            }

            List<Service> allServices = serviceController.getAllServices();
            List<Apartment> allApartments = apartmentController.getAllApartments();

            if (allApartments == null || allApartments.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Dữ liệu căn hộ rỗng!");
                return;
            }

            String apartmentID, customerName;
            if (hasMaCanHo) {
                apartmentID = selectedMaCanHo.toString();
                customerName = allApartments.stream()
                .filter(a -> a.getApartmentID().equals(apartmentID))
                .map(Apartment::getOwnerName)
                .findFirst()
                .orElse("");
            } else {
                customerName = selectedKhachHang.toString();
                apartmentID = allApartments.stream()
                .filter(a -> a.getOwnerName().equals(customerName))
                .map(Apartment::getApartmentID)
                .findFirst()
                .orElse("");
            }

            String cashier = txtThuNgan.getText().trim();
            Date selectedDate = dateNgayThanhToan.getDate();
            String paid = checkDaThanhToan.isSelected() ? "Đã thanh toán"
            : (checkChuaThanhToan.isSelected() ? "Chưa thanh toán" : "");

            if (apartmentID.isEmpty() || customerName.isEmpty() || cashier.isEmpty() || selectedDate == null || paid.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin.");
                return;
            }

            if (!Validator.isValidName(customerName)) {
                JOptionPane.showMessageDialog(this, "Tên khách hàng không hợp lệ.");
                return;
            }

            if (!Validator.isValidName(cashier)) {
                JOptionPane.showMessageDialog(this, "Tên thu ngân không hợp lệ.");
                return;
            }

            String invoiceDate = new SimpleDateFormat("dd/MM/yyyy").format(selectedDate);
            String invoiceID = IDGenerator.getInstance().generateInvoiceID();

            // Tìm Apartment và dịch vụ tương ứng
            Apartment matchedApartment = allApartments.stream()
            .filter(a -> a.getApartmentID().equals(apartmentID))
            .findFirst()
            .orElse(null);

            List<Service> selectedServices = new ArrayList<>();
            if (matchedApartment != null && matchedApartment.getDichVu() != null) {
                String[] serviceNames = matchedApartment.getDichVu().split(",");
                for (String name : serviceNames) {
                    String trimmed = name.trim();
                    allServices.stream()
                    .filter(s -> s.getServiceName().equalsIgnoreCase(trimmed))
                    .findFirst()
                    .ifPresent(selectedServices::add);
                }
            }

            // Tạo đối tượng hóa đơn
            Invoice invoice = new Invoice(
                invoiceID,
                apartmentID,
                customerName,
                cashier,
                invoiceDate,
                paid,
                selectedServices
            );

            // Tính tổng tiền hiển thị
            String tongTien = Invoice.getTotalAmountFormatted(matchedApartment, allServices);
            txtTongThanhToan.setText(tongTien);

            // Lưu hóa đơn
            controller.addInvoice(invoice);
            loadTable();
            clearForm();

            JOptionPane.showMessageDialog(this, "Thêm hóa đơn thành công!\n");

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm hóa đơn:\n" + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_butThemActionPerformed

    private void butXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butXoaActionPerformed
        List<Invoice> allInvoices = controller.getAllInvoices();
        if (allInvoices.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có hóa đơn nào để xóa.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        // B1: Lấy danh sách mã hóa đơn
        String[] invoiceIDs = allInvoices.stream()
        .map(Invoice::getInvoiceID)
        .distinct()
        .toArray(String[]::new);
        // B2: Hộp thoại chọn mã hóa đơn
        String selectedInvoiceID = (String) JOptionPane.showInputDialog(
            this,
            "Chọn mã hóa đơn để xóa:",
            "Xóa hóa đơn",
            JOptionPane.PLAIN_MESSAGE,
            null,
            invoiceIDs,
            invoiceIDs[0]
        );
        if (selectedInvoiceID == null || selectedInvoiceID.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Bạn chưa chọn mã hóa đơn.");
            return;
        }
        // B3: Xác nhận xóa hóa đơn đã chọn
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn xóa hóa đơn có mã: " + selectedInvoiceID + "?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            boolean result = controller.deleteInvoice(selectedInvoiceID.trim());
            if (result) {
                loadTable();
                clearForm();
                IDGenerator.getInstance().decreaseInvoiceID();
                JOptionPane.showMessageDialog(this, "Đã xóa hóa đơn " + selectedInvoiceID + " thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Xóa hóa đơn thất bại. Vui lòng thử lại.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_butXoaActionPerformed

    private void butTimKiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butTimKiemActionPerformed
        String[] options = {"Mã hóa đơn", "Mã căn hộ", "Tên khách hàng","Ngày thanh toán"};
        String selected = (String) JOptionPane.showInputDialog(
            this, "Chọn tiêu chí tìm kiếm:", "Tìm kiếm", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (selected == null) return;

        List<Invoice> list = new ArrayList<>();
        try {
            switch (selected) {
                case "Mã hóa đơn": {
                    String id = JOptionPane.showInputDialog(this, "Nhập mã hóa đơn:");
                    if (id != null && !id.trim().isEmpty()) {
                        Invoice invoice = controller.findByInvoiceID(id.trim());
                        if (invoice != null) list.add(invoice);
                    }
                    break;
                }
                case "Mã căn hộ": {
                    String maCanHo = JOptionPane.showInputDialog(this, "Nhập mã căn hộ:");
                    if (maCanHo != null && !maCanHo.trim().isEmpty()) {
                        list = controller.findByApartmentID(maCanHo.trim());
                    }
                    break;
                }
                case "Tên khách hàng": {
                    String name = JOptionPane.showInputDialog(this, "Nhập tên khách hàng:");
                    if (name != null && !name.trim().isEmpty()) {
                        list = controller.findByCustomerName(name.trim());
                    }
                    break;
                }
                case "Ngày thanh toán": {
                    String invoiceDate = JOptionPane.showInputDialog(this, "Nhập ngày thanh toán(dd/mm/yyyy):");
                    if (invoiceDate != null && !invoiceDate.trim().isEmpty()) {
                        list = controller.findByInvoiceDate(invoiceDate.trim());
                    }
                    break;
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm: " + ex.getMessage());
            return;
        }

        tableModel.setRowCount(0);
        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy kết quả.");
            return;
        }

        List<Service> services = serviceController.getAllServices();
        List<Apartment> apartments = apartmentController.getAllApartments();
        int stt = 1;
        for (Invoice invoice : list) {
            String ngay = (invoice.getInvoiceDate() != null && !invoice.getInvoiceDate().trim().isEmpty())
            ? invoice.getInvoiceDate()
            : "Chưa có";
            Apartment matchedApartment = apartments.stream()
            .filter(a -> a.getApartmentID().equals(invoice.getApartmentID()))
            .findFirst()
            .orElse(null);
            String totalFormatted = "0";
            if (matchedApartment != null) {
                totalFormatted = Invoice.getTotalAmountFormatted(matchedApartment, services);
            }
            String trangThai = invoice.isPaidAsBoolean() ? "Đã thanh toán" : "Chưa thanh toán";
            tableModel.addRow(new Object[]{
                stt++,
                invoice.getInvoiceID(),
                invoice.getApartmentID(),
                invoice.getCustomerName(),
                invoice.getCashierName(),
                ngay,
                totalFormatted,
                trangThai
            });
        }
    }//GEN-LAST:event_butTimKiemActionPerformed

    private void butSapXepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSapXepActionPerformed
        String[] criteriaOptions = {"Mã hóa đơn", "Mã căn hộ", "Khách hàng", "Ngày thanh toán"};
        String[] orderOptions = {"Tăng dần", "Giảm dần"};
        String selectedCriteria = (String) JOptionPane.showInputDialog(
            this,
            "Chọn tiêu chí sắp xếp:",
            "Sắp xếp hóa đơn",
            JOptionPane.QUESTION_MESSAGE,
            null,
            criteriaOptions,
            criteriaOptions[0]
        );
        if (selectedCriteria == null) return;

        String selectedOrder = (String) JOptionPane.showInputDialog(
            this,
            "Chọn thứ tự sắp xếp:",
            "Thứ tự sắp xếp",
            JOptionPane.QUESTION_MESSAGE,
            null,
            orderOptions,
            orderOptions[0]
        );
        if (selectedOrder == null) return;
        boolean ascending = selectedOrder.equals("Tăng dần");

        List<Invoice> sortedList = controller.sort(selectedCriteria, ascending);

        if (sortedList != null && !sortedList.isEmpty()) {
            tableModel.setRowCount(0);
            List<Service> services = serviceController.getAllServices();
            List<Apartment> apartments = apartmentController.getAllApartments();
            int stt = 1;
            for (Invoice invoice : sortedList) {
                String trangThai = invoice.isPaidAsBoolean() ? "Đã thanh toán" : "Chưa thanh toán";
                // Luôn tìm đúng căn hộ để lấy dịch vụ chính xác nhất
                Apartment matchedApartment = apartments.stream()
                .filter(a -> a.getApartmentID().equals(invoice.getApartmentID()))
                .findFirst()
                .orElse(null);
                String totalFormatted = (matchedApartment != null)
                ? Invoice.getTotalAmountFormatted(matchedApartment, services)
                : "0";
                String ngay = (invoice.getInvoiceDate() != null) ? invoice.getInvoiceDate() : "";
                tableModel.addRow(new Object[]{
                    stt++,
                    invoice.getInvoiceID(),
                    invoice.getApartmentID(),
                    invoice.getCustomerName(),
                    invoice.getCashierName(),
                    ngay,
                    totalFormatted,
                    trangThai
                });
            }
        } else {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để sắp xếp.");
        }
    }//GEN-LAST:event_butSapXepActionPerformed

    private void butLamMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butLamMoiActionPerformed
        loadTable();
        clearForm();
        JOptionPane.showMessageDialog(this, "Dữ liệu đã được làm mới.");
    }//GEN-LAST:event_butLamMoiActionPerformed

    private void butThanhToanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butThanhToanActionPerformed
        InvoiceManager invoiceManager = new InvoiceManager();
        ServiceManager serviceManager = new ServiceManager();
        ApartmentController apartmentController = new ApartmentController();

        List<Invoice> allInvoices = invoiceManager.getAll();
        List<Service> allServices = serviceManager.getAll();
        List<Apartment> allApartments = apartmentController.getAllApartments();

        Set<String> apartmentIDs = allInvoices.stream()
        .map(Invoice::getApartmentID)
        .collect(Collectors.toSet());

        if (apartmentIDs.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có hóa đơn nào để thanh toán.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String selectedApartmentID = (String) JOptionPane.showInputDialog(
            this,
            "Chọn mã căn hộ cần thanh toán:",
            "Chọn Căn Hộ",
            JOptionPane.PLAIN_MESSAGE,
            null,
            apartmentIDs.toArray(),
            apartmentIDs.iterator().next()
        );
        if (selectedApartmentID == null || selectedApartmentID.trim().isEmpty()) {
            return;
        }

        List<Invoice> relatedInvoices = allInvoices.stream()
        .filter(i -> i.getApartmentID().equals(selectedApartmentID))
        .collect(Collectors.toList());

        if (relatedInvoices.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn nào cho căn hộ này.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        StringBuilder invoiceText = new StringBuilder();
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        nf.setGroupingUsed(true);
        nf.setMaximumFractionDigits(0);

        // Tìm căn hộ tương ứng
        Apartment apartment = allApartments.stream()
        .filter(a -> a.getApartmentID().equals(selectedApartmentID))
        .findFirst()
        .orElse(null);

        // Lấy danh sách dịch vụ theo tên từ apartment.xml
        String[] dichVuNames = (apartment != null && apartment.getDichVu() != null)
        ? apartment.getDichVu().split(",") : new String[0];

        for (Invoice invoice : relatedInvoices) {
            invoiceText.append(centerText("HÓA ĐƠN DỊCH VỤ", 60)).append("\n");
            invoiceText.append("=".repeat(60)).append("\n");
            invoiceText.append(String.format("%-18s: %s\n", "Mã hóa đơn", invoice.getInvoiceID()));
            invoiceText.append(String.format("%-18s: %s\n", "Căn hộ", invoice.getApartmentID()));
            invoiceText.append(String.format("%-18s: %s\n", "Khách hàng", invoice.getCustomerName()));
            invoiceText.append(String.format("%-18s: %s\n", "Thu ngân", invoice.getCashierName()));
            invoiceText.append(String.format("%-18s: %s\n", "Ngày thanh toán", invoice.getInvoiceDate()));
            invoiceText.append("-".repeat(60)).append("\n");
            invoiceText.append(String.format("%-25s %5s %15s %15s\n", "Tên dịch vụ", "SL", "Đơn giá", "Thành tiền"));
            invoiceText.append("-".repeat(60)).append("\n");
            double totalAmount = 0;

            for (String name : dichVuNames) {
                String trimmedName = name.trim();
                Service matchedService = allServices.stream()
                .filter(s -> s.getServiceName().equalsIgnoreCase(trimmedName))
                .findFirst()
                .orElse(null);
                if (matchedService != null) {
                    double unitPrice = matchedService.getUnitPrice();
                    int quantity = 1; // Nếu sau này muốn mở rộng số lượng thì lấy theo apartment/hoặc trường khác
                    double thanhTien = unitPrice * quantity;
                    totalAmount += thanhTien;
                    invoiceText.append(String.format(
                        "%-25s %5d %,15.0f %,15.0f\n",
                        matchedService.getServiceName(), quantity, unitPrice, thanhTien));
            }
        }

        invoiceText.append("-".repeat(60)).append("\n");
        invoiceText.append(String.format("%-30s %,28.0f VNĐ\n", "TỔNG TIỀN:", totalAmount));
        invoiceText.append(String.format("%-30s %s\n", "TRẠNG THÁI:", invoice.isPaidAsBoolean() ? "Đã thanh toán" : "Chưa thanh toán"));
        invoiceText.append("=".repeat(60)).append("\n\n");
        }

        JTextArea textArea = new JTextArea(invoiceText.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(650, 400));
        JOptionPane.showMessageDialog(this, scrollPane, "HÓA ĐƠN CHI TIẾT", JOptionPane.INFORMATION_MESSAGE);
        }

        // Hàm căn giữa dòng chữ
        private String centerText(String text, int width) {
            int padSize = (width - text.length()) / 2;
            String pad = " ".repeat(Math.max(0, padSize));
            return pad + text;
    }//GEN-LAST:event_butThanhToanActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable DanhSachHoaDonThanhToan;
    private javax.swing.JButton butCapNhat;
    private javax.swing.JButton butClear;
    private javax.swing.JButton butLamMoi;
    private javax.swing.JButton butSapXep;
    private javax.swing.JButton butThanhToan;
    private javax.swing.JButton butThem;
    private javax.swing.JButton butTimKiem;
    private javax.swing.JButton butXoa;
    private javax.swing.JCheckBox checkChuaThanhToan;
    private javax.swing.JCheckBox checkDaThanhToan;
    private javax.swing.JComboBox<String> comboKhachHang;
    private javax.swing.JComboBox<String> comboMaCanHo;
    private com.toedter.calendar.JDateChooser dateNgayThanhToan;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbKhachHang;
    private javax.swing.JLabel lbMaCanHo;
    private javax.swing.JLabel lbNgayThanhToan;
    private javax.swing.JLabel lbThuNgan;
    private javax.swing.JLabel lbTongThanhToan;
    private javax.swing.JLabel lbTrangThai;
    private javax.swing.JTextField txtThuNgan;
    private javax.swing.JTextField txtTongThanhToan;
    // End of variables declaration//GEN-END:variables

    @Override
    public void reload() {
        controller = new InvoiceController();
        apartmentController = new ApartmentController();
        serviceController = new ServiceController();
        tableModel = (DefaultTableModel) DanhSachHoaDonThanhToan.getModel();
        try {
            loadCanHo();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi load dữ liệu căn hộ:\n" + e.getMessage());
        }
        // CheckBox chỉ chọn 1 trạng thái thanh toán
        checkDaThanhToan.addActionListener(e -> checkChuaThanhToan.setSelected(!checkDaThanhToan.isSelected()));
        checkChuaThanhToan.addActionListener(e -> checkDaThanhToan.setSelected(!checkChuaThanhToan.isSelected()));
        DanhSachHoaDonThanhToan.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = DanhSachHoaDonThanhToan.getSelectedRow();
                if (selectedRow >= 0) {
                    isUserSelectingCombo = false; // Tạm tắt sự kiện combo khi set dữ liệu
                    String invoiceID = (String) DanhSachHoaDonThanhToan.getValueAt(selectedRow, 1);
                    String apartmentID = (String) DanhSachHoaDonThanhToan.getValueAt(selectedRow, 2);
                    String customerName = (String) DanhSachHoaDonThanhToan.getValueAt(selectedRow, 3);
                    String cashierName = (String) DanhSachHoaDonThanhToan.getValueAt(selectedRow, 4);
                    String invoiceDateStr = (String) DanhSachHoaDonThanhToan.getValueAt(selectedRow, 5);
                    String totalAmount = (String) DanhSachHoaDonThanhToan.getValueAt(selectedRow, 6);
                    String status = (String) DanhSachHoaDonThanhToan.getValueAt(selectedRow, 7);

                    comboMaCanHo.setSelectedItem(apartmentID);
                    comboKhachHang.setSelectedItem(customerName);
                    txtThuNgan.setText(cashierName);
                    txtTongThanhToan.setText(totalAmount);
                    try {
                        if (invoiceDateStr != null && !invoiceDateStr.trim().isEmpty()) {
                            Date date = sdf.parse(invoiceDateStr);
                            dateNgayThanhToan.setDate(date);
                        } else {
                            dateNgayThanhToan.setDate(null);
                        }
                    } catch (Exception ex) {
                        dateNgayThanhToan.setDate(null);
                    }
                    if ("Đã thanh toán".equalsIgnoreCase(status)) {
                        checkDaThanhToan.setSelected(true);
                        checkChuaThanhToan.setSelected(false);
                    } else if ("Chưa thanh toán".equalsIgnoreCase(status)) {
                        checkDaThanhToan.setSelected(false);
                        checkChuaThanhToan.setSelected(true);
                    } else {
                        checkDaThanhToan.setSelected(false);
                        checkChuaThanhToan.setSelected(false);
                    }
                    isUserSelectingCombo = true; // Bật lại sự kiện combo
                }
            }
        });
        loadTable();
        // Set căn lề trái cho tất cả cột
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        for (int i = 0; i < DanhSachHoaDonThanhToan.getColumnCount(); i++) {
            DanhSachHoaDonThanhToan.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
        }
    }
}
