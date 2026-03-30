export const loginService = async (data: any) => {
  const response = await fetch(
    `${import.meta.env.VITE_API_BASE_URL}/auth/login`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(data),
    },
  );

  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message || "Đăng nhập thất bại");
  }

  return response.json();
};
export const getAccountService = async () => {
  const token = localStorage.getItem("access_token");

  const response = await fetch(
    `${import.meta.env.VITE_API_BASE_URL}/auth/account`,
    {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    },
  );

  if (!response.ok) {
    throw new Error("Không lấy được thông tin người dùng");
  }

  return response.json();
};
