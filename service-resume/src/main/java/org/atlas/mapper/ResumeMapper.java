package org.atlas.mapper;

import java.util.Collection;
import java.util.List;
import org.atlas.model.Resume;
import org.atlas.model.dto.ResumeDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ResumeMapper {
    ResumeDto mapToDto(Resume resume);

    Resume mapToEntity(ResumeDto resumeDto);

    List<ResumeDto> mapToDtoList(Collection<Resume> resumes);
}
