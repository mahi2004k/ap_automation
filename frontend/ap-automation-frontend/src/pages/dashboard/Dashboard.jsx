import { useEffect, useState } from "react";
import DashboardLayout from "../../layouts/DashboardLayout";
import { getDashboardData } from "../../api/dashboardApi";
import { toast } from "react-toastify";

import {
    LineChart,
    Line,
    BarChart,
    Bar,
    PieChart,
    Pie,
    Cell,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    Legend,
    ResponsiveContainer
} from "recharts";


function Dashboard() {

    const [dashboard, setDashboard] = useState(null);
    const [loading, setLoading] = useState(true);


    useEffect(() => {

        fetchDashboard();

    }, []);


    const fetchDashboard = async () => {

        try {

            const response = await getDashboardData();

            setDashboard(response.data);

        } catch (error) {

            console.error("Dashboard error:", error);

            toast.error("Failed to load dashboard data");

        } finally {

            setLoading(false);

        }

    };


    // -----------------------------------------
    // Loading
    // -----------------------------------------

    if (loading) {

        return (

            <DashboardLayout>

                <div
                    className="d-flex flex-column justify-content-center align-items-center"
                    style={{ minHeight: "60vh" }}
                >

                    <div
                        className="spinner-border text-primary"
                        role="status"
                    />

                    <p className="text-muted mt-3">
                        Loading dashboard...
                    </p>

                </div>

            </DashboardLayout>

        );

    }


    // -----------------------------------------
    // Safe dynamic data
    // -----------------------------------------

    const invoiceTrend =
        dashboard?.invoiceTrend || [];

    const paymentTrend =
        dashboard?.paymentTrend || [];

    const invoiceStatus =
        dashboard?.invoiceStatus || [];

    const recentInvoices =
        dashboard?.recentInvoices || [];


    // -----------------------------------------
    // Invoice status colors
    // -----------------------------------------

    const STATUS_COLORS = [
        "#198754",
        "#ffc107",
        "#dc3545",
        "#0d6efd",
        "#6f42c1"
    ];


    // -----------------------------------------
    // Currency formatter
    // -----------------------------------------

    const formatCurrency = (amount) => {

        if (amount === null || amount === undefined) {
            return "₹0";
        }

        return new Intl.NumberFormat("en-IN", {
            style: "currency",
            currency: "INR",
            maximumFractionDigits: 0
        }).format(amount);

    };


    // -----------------------------------------
    // Date formatter
    // -----------------------------------------

    const formatDate = (date) => {

        if (!date) {
            return "-";
        }

        return new Date(date).toLocaleDateString(
            "en-IN",
            {
                day: "2-digit",
                month: "short",
                year: "numeric"
            }
        );

    };


    // -----------------------------------------
    // Status badge
    // -----------------------------------------

    const getStatusBadge = (status) => {

        switch (status?.toUpperCase()) {

            case "PAID":

                return (
                    <span className="badge bg-primary">
                        Paid
                    </span>
                );


            case "APPROVED":

                return (
                    <span className="badge bg-success">
                        Approved
                    </span>
                );


            case "PENDING":

                return (
                    <span className="badge bg-warning text-dark">
                        Pending
                    </span>
                );


            case "REJECTED":

                return (
                    <span className="badge bg-danger">
                        Rejected
                    </span>
                );


            case "PROCESSING":

                return (
                    <span className="badge bg-info text-dark">
                        Processing
                    </span>
                );


            default:

                return (
                    <span className="badge bg-secondary">
                        {status || "Unknown"}
                    </span>
                );

        }

    };


    // -----------------------------------------
    // Dashboard
    // -----------------------------------------

    return (

        <DashboardLayout>

            {/* ========================================
                HEADER
            ======================================== */}

            <div className="d-flex justify-content-between align-items-center mb-4">

                <div>

                    <h2 className="fw-bold mb-1">
                        Dashboard
                    </h2>

                    <p className="text-muted mb-0">
                        Accounts Payable Analytics
                    </p>

                </div>


                <div className="text-muted">

                    {new Date().toLocaleDateString(
                        "en-IN",
                        {
                            weekday: "short",
                            day: "2-digit",
                            month: "short",
                            year: "numeric"
                        }
                    )}

                </div>

            </div>


            {/* ========================================
                KPI CARDS
            ======================================== */}

            <div className="row g-4 mb-4">


                {/* Purchase Orders */}

                <div className="col-xl-3 col-md-6">

                    <div className="card border-0 shadow-sm h-100">

                        <div className="card-body">

                            <div className="d-flex justify-content-between align-items-center">

                                <div>

                                    <p className="text-muted mb-1">
                                        Purchase Orders
                                    </p>

                                    <h2 className="fw-bold mb-1">

                                        {dashboard?.totalPurchaseOrders || 0}

                                    </h2>

                                    <small className="text-muted">
                                        Total purchase orders
                                    </small>

                                </div>


                                <div className="bg-primary bg-opacity-10 rounded-3 p-3">

                                    <i className="bi bi-cart-check fs-3 text-primary"></i>

                                </div>

                            </div>

                        </div>

                    </div>

                </div>


                {/* Total Invoices */}

                <div className="col-xl-3 col-md-6">

                    <div className="card border-0 shadow-sm h-100">

                        <div className="card-body">

                            <div className="d-flex justify-content-between align-items-center">

                                <div>

                                    <p className="text-muted mb-1">
                                        Total Invoices
                                    </p>

                                    <h2 className="fw-bold mb-1">

                                        {dashboard?.totalInvoices || 0}

                                    </h2>

                                    <small className="text-muted">
                                        All processed invoices
                                    </small>

                                </div>


                                <div className="bg-success bg-opacity-10 rounded-3 p-3">

                                    <i className="bi bi-receipt fs-3 text-success"></i>

                                </div>

                            </div>

                        </div>

                    </div>

                </div>


                {/* Pending Approvals */}

                <div className="col-xl-3 col-md-6">

                    <div className="card border-0 shadow-sm h-100">

                        <div className="card-body">

                            <div className="d-flex justify-content-between align-items-center">

                                <div>

                                    <p className="text-muted mb-1">
                                        Pending Approvals
                                    </p>

                                    <h2 className="fw-bold mb-1">

                                        {dashboard?.pendingInvoices || 0}

                                    </h2>

                                    <small className="text-warning">
                                        Requires attention
                                    </small>

                                </div>


                                <div className="bg-warning bg-opacity-10 rounded-3 p-3">

                                    <i className="bi bi-clock-history fs-3 text-warning"></i>

                                </div>

                            </div>

                        </div>

                    </div>

                </div>


                {/* Completed Payments */}

                <div className="col-xl-3 col-md-6">

                    <div className="card border-0 shadow-sm h-100">

                        <div className="card-body">

                            <div className="d-flex justify-content-between align-items-center">

                                <div>

                                    <p className="text-muted mb-1">
                                        Completed Payments
                                    </p>

                                    <h2 className="fw-bold mb-1">

                                        {dashboard?.completedPayments || 0}

                                    </h2>

                                    <small className="text-muted">
                                        Successfully paid
                                    </small>

                                </div>


                                <div className="bg-info bg-opacity-10 rounded-3 p-3">

                                    <i className="bi bi-credit-card fs-3 text-info"></i>

                                </div>

                            </div>

                        </div>

                    </div>

                </div>

            </div>


            {/* ========================================
                SECONDARY KPI CARDS
            ======================================== */}

            <div className="row g-4 mb-4">


                <div className="col-md-4">

                    <div className="card border-0 shadow-sm">

                        <div className="card-body">

                            <p className="text-muted mb-1">
                                Total Payable Amount
                            </p>

                            <h3 className="fw-bold">

                                {formatCurrency(
                                    dashboard?.totalPayableAmount
                                )}

                            </h3>

                        </div>

                    </div>

                </div>


                <div className="col-md-4">

                    <div className="card border-0 shadow-sm">

                        <div className="card-body">

                            <p className="text-muted mb-1">
                                Paid Amount
                            </p>

                            <h3 className="fw-bold text-success">

                                {formatCurrency(
                                    dashboard?.totalPaidAmount
                                )}

                            </h3>

                        </div>

                    </div>

                </div>


                <div className="col-md-4">

                    <div className="card border-0 shadow-sm">

                        <div className="card-body">

                            <p className="text-muted mb-1">
                                Pending Amount
                            </p>

                            <h3 className="fw-bold text-warning">

                                {formatCurrency(
                                    dashboard?.totalPendingAmount
                                )}

                            </h3>

                        </div>

                    </div>

                </div>

            </div>


            {/* ========================================
                INVOICE TREND + STATUS
            ======================================== */}

            <div className="row g-4 mb-4">


                {/* Invoice Trend */}

                <div className="col-lg-8">

                    <div className="card border-0 shadow-sm h-100">

                        <div className="card-body">

                            <div className="mb-3">

                                <h5 className="fw-bold mb-1">
                                    Invoice Processing Trend
                                </h5>

                                <p className="text-muted mb-0">
                                    Monthly invoice activity
                                </p>

                            </div>


                            <ResponsiveContainer
                                width="100%"
                                height={320}
                            >

                                <LineChart
                                    data={invoiceTrend}
                                >

                                    <CartesianGrid
                                        strokeDasharray="3 3"
                                    />

                                    <XAxis
                                        dataKey="month"
                                    />

                                    <YAxis />

                                    <Tooltip />

                                    <Legend />


                                    <Line
                                        type="monotone"
                                        dataKey="invoices"
                                        name="Invoices"
                                        stroke="#0d6efd"
                                        strokeWidth={3}
                                        dot={{ r: 4 }}
                                        activeDot={{ r: 7 }}
                                    />

                                </LineChart>

                            </ResponsiveContainer>

                        </div>

                    </div>

                </div>


                {/* Invoice Status */}

                <div className="col-lg-4">

                    <div className="card border-0 shadow-sm h-100">

                        <div className="card-body">

                            <h5 className="fw-bold mb-1">
                                Invoice Status
                            </h5>

                            <p className="text-muted">
                                Current invoice distribution
                            </p>


                            {invoiceStatus.length > 0 ? (

                                <ResponsiveContainer
                                    width="100%"
                                    height={280}
                                >

                                    <PieChart>

                                        <Pie
                                            data={invoiceStatus}
                                            cx="50%"
                                            cy="50%"
                                            outerRadius={90}
                                            dataKey="value"
                                            nameKey="name"
                                            label
                                        >

                                            {invoiceStatus.map(
                                                (entry, index) => (

                                                    <Cell
                                                        key={`cell-${index}`}
                                                        fill={
                                                            STATUS_COLORS[
                                                                index %
                                                                STATUS_COLORS.length
                                                            ]
                                                        }
                                                    />

                                                )
                                            )}

                                        </Pie>


                                        <Tooltip />

                                        <Legend />

                                    </PieChart>

                                </ResponsiveContainer>

                            ) : (

                                <div className="text-center text-muted py-5">

                                    No invoice status data available.

                                </div>

                            )}

                        </div>

                    </div>

                </div>

            </div>


            {/* ========================================
                PAYMENT ANALYTICS
            ======================================== */}

            <div className="row g-4 mb-4">

                <div className="col-12">

                    <div className="card border-0 shadow-sm">

                        <div className="card-body">

                            <h5 className="fw-bold mb-1">
                                Payment Analytics
                            </h5>

                            <p className="text-muted">
                                Monthly payment amount
                            </p>


                            <ResponsiveContainer
                                width="100%"
                                height={320}
                            >

                                <BarChart
                                    data={paymentTrend}
                                >

                                    <CartesianGrid
                                        strokeDasharray="3 3"
                                    />

                                    <XAxis
                                        dataKey="month"
                                    />

                                    <YAxis />


                                    <Tooltip
                                        formatter={(value) =>
                                            formatCurrency(value)
                                        }
                                    />


                                    <Legend />


                                    <Bar
                                        dataKey="amount"
                                        name="Payment Amount"
                                        fill="#198754"
                                        radius={[
                                            6,
                                            6,
                                            0,
                                            0
                                        ]}
                                    />

                                </BarChart>

                            </ResponsiveContainer>

                        </div>

                    </div>

                </div>

            </div>


            {/* ========================================
                RECENT INVOICES
            ======================================== */}

            <div className="card border-0 shadow-sm mb-4">

                <div className="card-body">


                    <div className="d-flex justify-content-between align-items-center mb-3">

                        <div>

                            <h5 className="fw-bold mb-1">
                                Recent Invoices
                            </h5>

                            <p className="text-muted mb-0">
                                Latest invoice processing activity
                            </p>

                        </div>


                        <button
                            className="btn btn-outline-primary btn-sm"
                        >
                            View All
                        </button>

                    </div>


                    <div className="table-responsive">

                        <table className="table table-hover align-middle">

                            <thead className="table-light">

                                <tr>

                                    <th>
                                        Invoice
                                    </th>

                                    <th>
                                        Vendor
                                    </th>

                                    <th>
                                        Amount
                                    </th>

                                    <th>
                                        Status
                                    </th>

                                    <th>
                                        Date
                                    </th>

                                </tr>

                            </thead>


                            <tbody>


                                {recentInvoices.length > 0 ? (

                                    recentInvoices.map(
                                        (invoice, index) => (

                                            <tr
                                                key={
                                                    invoice.id ||
                                                    index
                                                }
                                            >

                                                <td>

                                                    <strong>
                                                        {
                                                            invoice.invoiceNumber ||
                                                            "-"
                                                        }
                                                    </strong>

                                                </td>


                                                <td>

                                                    {
                                                        invoice.vendorName ||
                                                        "-"
                                                    }

                                                </td>


                                                <td>

                                                    {formatCurrency(
                                                        invoice.amount
                                                    )}

                                                </td>


                                                <td>

                                                    {getStatusBadge(
                                                        invoice.status
                                                    )}

                                                </td>


                                                <td>

                                                    {formatDate(
                                                        invoice.date
                                                    )}

                                                </td>

                                            </tr>

                                        )
                                    )

                                ) : (

                                    <tr>

                                        <td
                                            colSpan="5"
                                            className="text-center text-muted py-4"
                                        >

                                            No recent invoices found.

                                        </td>

                                    </tr>

                                )}

                            </tbody>

                        </table>

                    </div>

                </div>

            </div>


        </DashboardLayout>

    );

}


export default Dashboard;