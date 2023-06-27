import { useCallback } from "react";

import { useUi } from "../contexts/UiContext";
import { useUuid } from "../contexts/UuidContext";

type HttpMethod = "GET" | "POST";

interface FetchConfig {
  url: string;
  method?: HttpMethod;
  body?: unknown;
  showSuccess?: boolean;
  showDuration?: boolean;
  expectJson?: boolean;
}

interface ApiResponse<T> {
  data?: T;
  success: boolean;
}

const useFetchWithUiFeedback = () => {
  const { setOpenNotify, setNotifyStatus, setOpenBack } = useUi();
  const { uuid } = useUuid();

  const fetchWithUiFeedback = useCallback(
    async <T>({
      url,
      method = "GET",
      body,
      showSuccess = true,
      showDuration = false,
      expectJson = true,
    }: FetchConfig): Promise<ApiResponse<T>> => {
      const headers: HeadersInit = { "Content-Type": "application/json" };
      const requestBody = method === "POST" ? JSON.stringify(body) : undefined;

      try {
        setOpenBack(true);
        const startTime = Date.now();
        const response = await fetch(`${url}?client_id=${uuid}`, {
          method,
          headers,
          body: requestBody,
        });
        const endTime = Date.now();
        if (!response.ok) {
          setOpenBack(false);
          const message =
            (await response.text()) || "Network response was not ok";
          setNotifyStatus({ message, severity: "error" });
          setOpenNotify(true);
          return { success: false };
        }
        setOpenBack(false);
        if (showSuccess && !showDuration) {
          setNotifyStatus({ message: "Success!", duration: 2000 });
          setOpenNotify(true);
        } else if (showSuccess && showDuration) {
          setNotifyStatus({
            message: `Success! (${endTime - startTime}ms)`,
            // duration: 2000,
          });
          setOpenNotify(true);
        }
        return {
          data: expectJson ? await response.json() : undefined,
          success: true,
        };
      } catch (error) {
        setOpenBack(false);
        const message =
          error instanceof Error ? error.message : "Unknown error occurred";
        setNotifyStatus({ message, severity: "error" });
        setOpenNotify(true);
        return { success: false };
      }
    },
    [setOpenNotify, setNotifyStatus, setOpenBack, uuid]
  );

  return fetchWithUiFeedback;
};

export default useFetchWithUiFeedback;
