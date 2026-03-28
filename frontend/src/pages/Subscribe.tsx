import { type JSX, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import ApiKeySubmit from "@/features/subscribe/components/ApiKeySubmit";
import "@styles/pages/Subscribe.css";
import { useRegistration } from "@/context/RegistrationContext";
import { Toaster } from "react-hot-toast";

const SubscribeFlow = () => {
  const { registrationData } = useRegistration();
  const navigate = useNavigate();

  useEffect(() => {
    // Chỉ chạy khi apiKey thực sự tồn tại
    if (registrationData?.apiKey) {
      // Đảm bảo không redirect nếu đã ở đúng trang
      if (window.location.pathname !== "/login") {
        navigate("/login", { replace: true }); // Dùng replace để tránh rác lịch sử duyệt web
      }
    }
  }, [registrationData?.apiKey, navigate]); // ĐIỂM QUAN TRỌNG: Chỉ phụ thuộc vào biến chuỗi này

  const handleKeyValidated = () => {
    if (window.location.pathname !== "/login") {
      navigate("/login", { replace: true });
    }
  };

  return <ApiKeySubmit onNext={handleKeyValidated} />;
};
// ... phần còn lại giữ nguyên

const Subscribe = (): JSX.Element => {
  return (
    <>
      <Toaster position="top-center" reverseOrder={false} />
      <div className="subscribe">
        <SubscribeFlow />
      </div>
    </>
  );
};

export default Subscribe;
