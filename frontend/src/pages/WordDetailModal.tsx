import { useEffect, useState } from "react";
import ReactMarkdown from "react-markdown";
import { useDictionary } from "@/features/dictionary/hooks/useDictionary";
import { getWordAudioService } from "@/features/dictionary/services/audioService";
import toast from "react-hot-toast";
import "../styles/components/WordDetailModal.css";

interface WordDetailModalProps {
  word: string;
  onClose: () => void;
}

const WordDetailModal = ({ word, onClose }: WordDetailModalProps) => {
  const { data, isLoading, error, fetchTranslation } = useDictionary();
  const [audioUrl, setAudioUrl] = useState<string | null>(null);

  useEffect(() => {
    const initData = async () => {
      const storedData = localStorage.getItem("userRegistrationData");
      let apiKey = "";
      if (storedData) {
        apiKey = JSON.parse(storedData).apiKey || "";
      }

      if (!apiKey) {
        toast.error("Không tìm thấy API Key.");
        onClose();
        return;
      }

      await Promise.all([
        fetchTranslation({ word, apiKey }),
        getWordAudioService(word)
          .then((url) => setAudioUrl(url))
          .catch(() => setAudioUrl(null)),
      ]);
    };

    if (word) initData();
  }, [word, fetchTranslation, onClose]);

  const playAudio = () => {
    if (audioUrl) new Audio(audioUrl).play();
  };

  if (isLoading)
    return (
      <div className="modal-overlay">
        <div className="loader">Đang dịch...</div>
      </div>
    );
  if (!data) return null;

  const { explanation } = data;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="word-modal-content" onClick={(e) => e.stopPropagation()}>
        <button className="close-btn" onClick={onClose}>
          &times;
        </button>

        <div className="modal-header">
          <h2>{data.word}</h2>
          <div className="pronunciation-row">
            <span className="phonetic">{explanation.pronunciation}</span>
            {audioUrl && (
              <button
                className="audio-btn"
                onClick={playAudio}
                aria-label="Play pronunciation"
                title="Play pronunciation"
              >
                <svg
                  viewBox="0 0 24 24"
                  width="20"
                  height="20"
                  fill="currentColor"
                  aria-hidden="true"
                >
                  <path d="M3 9v6h4l5 5V4L7 9H3zm13.5 3c0-1.77-1.02-3.29-2.5-4.03v8.05c1.48-.73 2.5-2.25 2.5-4.02zM14 3.23v2.06c2.89.86 5 3.54 5 6.71s-2.11 5.85-5 6.71v2.06c4.01-.91 7-4.49 7-8.77s-2.99-7.86-7-8.77z" />
                </svg>
              </button>
            )}
          </div>
        </div>

        <div className="modal-body markdown-container">
          <section>
            <h3> Ý nghĩa</h3>
            <ReactMarkdown>{explanation.meaning || ""}</ReactMarkdown>
          </section>

          <section>
            <h3> Ngữ pháp & Cách dùng</h3>
            <ReactMarkdown>{explanation.grammarUsage || ""}</ReactMarkdown>
          </section>

          {explanation.summary && (
            <section className="summary-box">
              <ReactMarkdown>{`**Tóm tắt:** ${explanation.summary}`}</ReactMarkdown>
            </section>
          )}
        </div>
      </div>
    </div>
  );
};

export default WordDetailModal;
