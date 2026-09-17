import { UploadBox } from "@/modules/core/components";
import { Box, HStack, Image, Spinner, VStack } from "@chakra-ui/react";
interface ItemImageInputProps {
  image: File | null;
  setImage: (file: File | null) => void;
  loading: boolean;
  placeholder: string;
}
export function ItemImageInput({
  image,
  setImage,
  loading,
  placeholder,
}: ItemImageInputProps) {
  return (
    <HStack
      align="stretch"
      w="100%"
      maxW="100%"
      maxH="200px"
      mb={image ? "60px" : ""}
    >
      <Box
        aspectRatio={2 / 3}
        h="auto"
        maxH="100%"
        flexShrink={0}
        alignItems="center"
        justifyContent="center"
        display="flex"
        backgroundColor="gray.100"
        borderRadius="md"
      >
        {loading ? (
          <Spinner size="lg" color="principal.500" />
        ) : (
          <Image
            src={image ? URL.createObjectURL(image) : placeholder}
            alt="Foto del juego de rol"
            w="100%"
            h="100%"
            objectFit="cover"
          />
        )}
      </Box>
      <VStack flex={1} minW={0}>
        <UploadBox
          text={
            <>
              Arrastra la <b>foto del juego de rol</b>
            </>
          }
          secondaryText="JPG o PNG, tamaño no superior a 2MB"
          fileType="image/*"
          onFileChange={setImage}
          disabled={loading}
        />
      </VStack>
    </HStack>
  );
}
