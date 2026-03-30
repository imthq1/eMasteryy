import { type JSX } from "react";
import { Routes, Route, Navigate } from "react-router-dom";
import ProtectedRoute from "./ProtectedRoute";
import GuestRoute from "./GuestRoute";
import MainLayout from "@components/layouts/MainLayout";
import GuestLayout from "@components/layouts/GuestLayout";
import NotFoundPage from "@pages/NotFoundPage";
import Home from "@pages/Home";
import Welcome from "@pages/Welcome";
import Subscribe from "@pages/Subscribe";
import Chatbot from "@pages/Chatbot";
import Quiz from "@pages/Quiz";
import Dictionary from "@/pages/Dictionary";
import LoginPage from "@/pages/LoginPage";
import CollectionsPage from "@/pages/CollectionsPage";
import CollectionDetailPage from "@/pages/CollectionDetailPage";

const AppRoutes = (): JSX.Element => {
  return (
    <Routes>
      <Route
        path="welcome"
        element={
          <GuestRoute>
            <GuestLayout />
          </GuestRoute>
        }
      >
        <Route index element={<Welcome />} />
      </Route>

      <Route path="subscribe" element={<Subscribe />} />

      <Route
        path="login"
        element={
          <GuestRoute>
            <LoginPage />
          </GuestRoute>
        }
      />

      <Route
        path="/"
        element={
          <ProtectedRoute>
            <MainLayout />
          </ProtectedRoute>
        }
      >
        <Route index element={<Home />} />
        <Route path="chatbot" element={<Chatbot />} />
        {/* Đã xóa /login khỏi khu vực này */}
        <Route path="quiz" element={<Quiz />} />
        <Route path="dictionary" element={<Dictionary />} />
        <Route path="/collections" element={<CollectionsPage />} />
        <Route
          path="/collections/:collectionId"
          element={<CollectionDetailPage />}
        />
      </Route>

      {/* 404 Not Found */}
      <Route path="/404" element={<NotFoundPage />} />
      <Route path="*" element={<Navigate to="/404" replace />} />
    </Routes>
  );
};

export default AppRoutes;
