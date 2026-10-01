import { Link, Text } from "@chakra-ui/react";
import { useEffect, useRef, useState } from "react";

interface ItemDescriptionProps {
  description: string | undefined;
}

export function ItemDescription({ description }: ItemDescriptionProps) {
  const [descriptionExpanded, setDescriptionExpanded] = useState(false);
  const descriptionRef = useRef<HTMLParagraphElement>(null);
  const [isDescriptionOverflowing, setIsDescriptionOverflowing] =
    useState(false);

  useEffect(() => {
    const element = descriptionRef.current;
    if (!element) return;
    setIsDescriptionOverflowing(element.scrollHeight > element.clientHeight);
  }, [description, descriptionExpanded]);

  if (!description) {
    return null;
  }

  return (
    <>
      <Text
        ref={descriptionRef}
        wordBreak="break-word"
        overflowWrap="break-word"
        lang="es"
        hyphens="auto"
        lineClamp={descriptionExpanded ? undefined : 5}
      >
        {description}
      </Text>

      {(descriptionExpanded || isDescriptionOverflowing) && (
        <Link onClick={() => setDescriptionExpanded(!descriptionExpanded)}>
          {descriptionExpanded ? "Ver menos" : "Ver más"}
        </Link>
      )}
    </>
  );
}
