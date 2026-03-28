import { useState, useEffect, type ReactNode, type JSX } from "react";
import { Navigate } from "react-router-dom";
import { useRegistration } from "@/context/RegistrationContext";
import { useApiKeyCheck } from "@features/subscribe/hooks/useApiKeyCheck";
import Loader from "@/components/common/Loader";

interface ProtectedRouteProps {
  children: ReactNode;
}

const ProtectedRoute = ({ children }: ProtectedRouteProps): JSX.Element => {
  const [isVerifying, setIsVerifying] = useState(true);
  const [isAllowed, setIsAllowed] = useState(false);

  // Thêm state để biết chính xác nên điều hướng người dùng về đâu nếu bị chặn
  const [redirectPath, setRedirectPath] = useState("/login");

  const { registrationData, updateRegistrationData } = useRegistration();
  const { validateApiKey } = useApiKeyCheck();

  useEffect(() => {
    const verifyAccess = async () => {
      // 1. Kiểm tra Authentication (Người dùng đã đăng nhập chưa?)
      const accessToken = localStorage.getItem("access_token");
      if (!accessToken) {
        setRedirectPath("/login");
        setIsAllowed(false);
        setIsVerifying(false);
        return;
      }

      // 2. Kiểm tra sự tồn tại của Gemini API Key
      const { apiKey } = registrationData;
      if (!apiKey) {
        setRedirectPath("/subscribe"); // Hoặc "/welcome" tùy thuộc vào route nhập key của bạn
        setIsAllowed(false);
        setIsVerifying(false);
        return;
      }

      // 3. Xác thực tính hợp lệ của API Key
      const isKeyValid = await validateApiKey(apiKey);
      if (isKeyValid) {
        setIsAllowed(true);
      } else {
        // Key không hợp lệ -> Xóa key cũ và bắt nhập lại
        updateRegistrationData({ apiKey: undefined });
        setRedirectPath("/subscribe");
        setIsAllowed(false);
      }

      setIsVerifying(false);
    };

    verifyAccess();
  }, [registrationData?.apiKey, validateApiKey, updateRegistrationData]);
  // Điểm lưu ý: Chỉ theo dõi apiKey cụ thể thay vì toàn bộ object registrationData để tránh re-render vô tận

  if (isVerifying) {
    return <Loader />;
  }

  return isAllowed ? <>{children}</> : <Navigate to={redirectPath} replace />;
};

export default ProtectedRoute;
